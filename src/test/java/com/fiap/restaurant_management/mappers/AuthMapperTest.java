package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AuthResultDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperTest {

    private AuthMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(AuthMapper.class);
    }

    @Test
    void shouldMapAuthResultToLoginResponse() {
        LocalDateTime expiresAt = LocalDateTime.now();

        AuthResultDTO dto = new AuthResultDTO(
                "token123",
                expiresAt
        );

        UsersLoginResponseDTO response = mapper.toLoginResponseDTO(dto);

        assertNotNull(response);
        assertEquals("token123", response.accessToken());
        assertEquals(expiresAt, response.expiresAt());
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        UsersLoginResponseDTO response = mapper.toLoginResponseDTO(null);

        assertNull(response);
    }

    @Test
    void shouldMapNullFields() {
        AuthResultDTO dto = new AuthResultDTO(
                null,
                null
        );

        UsersLoginResponseDTO response = mapper.toLoginResponseDTO(dto);

        assertNotNull(response);
        assertNull(response.accessToken());
        assertNull(response.expiresAt());
    }
}