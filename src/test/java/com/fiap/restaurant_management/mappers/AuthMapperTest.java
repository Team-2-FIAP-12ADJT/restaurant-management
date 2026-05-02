package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AuthResultDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperTest {

    private AuthMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new AuthMapperImpl();
    }

    @Test
    void shouldMapAuthResultToLoginResponse() {
        LocalDateTime expiresAt = LocalDateTime.of(2026, 5, 2, 12, 0);
        AuthResultDTO authResult = new AuthResultDTO("token-123", expiresAt);

        UsersLoginResponseDTO response = mapper.toLoginResponseDTO(authResult);

        assertNotNull(response);
        assertEquals("token-123", response.accessToken());
        assertEquals(expiresAt, response.expiresAt());
    }

    @Test
    void shouldReturnNullWhenAuthResultIsNull() {
        assertNull(mapper.toLoginResponseDTO(null));
    }
}
