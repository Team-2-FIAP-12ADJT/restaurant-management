package com.fiap.restaurant_management.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    void shouldGenerateTokenWithUserIdSubjectAndRolesClaim() {
        TokenService service = new TokenService(jwtEncoder);
        UUID userId = UUID.randomUUID();
        Users user = new Users();
        user.setId(userId);
        user.setRole(RoleEnum.OWNER);

        Jwt encodedJwt = Jwt.withTokenValue("encoded-token")
                .header("alg", "HS256")
                .subject(userId.toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();
        when(jwtEncoder.encode(any())).thenReturn(encodedJwt);

        UsersLoginResponseDTO response = service.generateToken(user);

        assertEquals("encoded-token", response.accessToken());
        assertNotNull(response.expiresAt());

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtEncoderParameters parameters = parametersCaptor.getValue();
        assertEquals(userId.toString(), parameters.getClaims().getSubject());
        assertEquals(java.util.List.of("OWNER"), parameters.getClaims().getClaim("roles"));
        assertEquals("HS256", parameters.getJwsHeader().getAlgorithm().getName());
    }
}
