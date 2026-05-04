package com.fiap.restaurant_management.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
    public boolean isSelf(UUID userId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }

        return userId.toString().equals(jwt.getSubject());
    }
}
