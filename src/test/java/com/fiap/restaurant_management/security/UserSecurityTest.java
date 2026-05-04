package com.fiap.restaurant_management.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class UserSecurityTest {

    private final UserSecurity userSecurity = new UserSecurity();

    @Test
    void shouldReturnTrueWhenJwtSubjectMatchesUserId() {
        UUID userId = UUID.randomUUID();

        assertTrue(userSecurity.isSelf(userId, new JwtAuthenticationToken(jwtWithSubject(userId.toString()))));
    }

    @Test
    void shouldReturnFalseWhenAuthenticationIsNull() {
        assertFalse(userSecurity.isSelf(UUID.randomUUID(), null));
    }

    @Test
    void shouldReturnFalseWhenPrincipalIsNotJwt() {
        var authentication = new UsernamePasswordAuthenticationToken("principal", "credentials");

        assertFalse(userSecurity.isSelf(UUID.randomUUID(), authentication));
    }

    @Test
    void shouldReturnFalseWhenJwtSubjectDoesNotMatchUserId() {
        assertFalse(userSecurity.isSelf(UUID.randomUUID(), new JwtAuthenticationToken(jwtWithSubject(UUID.randomUUID().toString()))));
    }

    private Jwt jwtWithSubject(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject(subject)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
    }
}
