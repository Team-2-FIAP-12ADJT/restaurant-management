package com.fiap.restaurant_management.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.time.ZoneId;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.entities.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

    public UsersLoginResponseDTO generateToken(Users user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(15, ChronoUnit.MINUTES); // Token válido por 15 minutos

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .claim("roles", List.of(user.getRole().name()))
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
        return new UsersLoginResponseDTO(token, LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
    }
}
