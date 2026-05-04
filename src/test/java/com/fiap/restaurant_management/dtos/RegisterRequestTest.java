package com.fiap.restaurant_management.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

class RegisterRequestTest {

    @Test
    void shouldTrimAndNormalizeFields() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "  Password@123  ",
                "  Gustavo  ",
                "  login123  ",
                "  EMAIL@TEST.COM  ",
                List.of(mock(AddressRequestDTO.class)));

        assertEquals("Password@123", dto.password());
        assertEquals("Gustavo", dto.name());
        assertEquals("login123", dto.login());
        assertEquals("email@test.com", dto.email());
    }

    @Test
    void shouldHandleNullValues() {
        RegisterRequestDTO dto = new RegisterRequestDTO(null, null, null, null, null);

        assertNull(dto.password());
        assertNull(dto.name());
        assertNull(dto.login());
        assertNull(dto.email());
        assertNull(dto.address());
    }
}
