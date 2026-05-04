package com.fiap.restaurant_management.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Test
    void shouldCreateBCryptPasswordEncoder() {
        assertTrue(config.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    @Test
    void shouldConvertRolesClaimToSpringAuthorities() {
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("user-id")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("roles", List.of("OWNER", "CLIENT"))
                .build();

        Authentication authentication = converter.convert(jwt);

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER")));
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT")));
    }
}
