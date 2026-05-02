package com.fiap.restaurant_management.dtos;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorResponseTest {

    @Test
    void shouldCreateDtoWithAllFields() {
        Map<String, String> errors = Map.of(
                "name", "Name is required",
                "email", "Invalid email"
        );

        var dto = new ValidationErrorResponseDTO(
                "https://example.com/validation-error",
                "Validation Failed",
                400,
                "Invalid request data",
                "/users",
                errors
        );

        assertEquals("https://example.com/validation-error", dto.type());
        assertEquals("Validation Failed", dto.title());
        assertEquals(400, dto.status());
        assertEquals("Invalid request data", dto.detail());
        assertEquals("/users", dto.instance());
        assertEquals(errors, dto.errors());
    }

    @Test
    void shouldAllowNullFields() {
        var dto = new ValidationErrorResponseDTO(
                null,
                null,
                0,
                null,
                null,
                null
        );

        assertNull(dto.type());
        assertNull(dto.title());
        assertEquals(0, dto.status());
        assertNull(dto.detail());
        assertNull(dto.instance());
        assertNull(dto.errors());
    }

    @Test
    void shouldStoreErrorsMapCorrectly() {
        Map<String, String> errors = Map.of(
                "password", "Password is required"
        );

        var dto = new ValidationErrorResponseDTO(
                "type",
                "title",
                400,
                "detail",
                "instance",
                errors
        );

        assertTrue(dto.errors().containsKey("password"));
        assertEquals("Password is required", dto.errors().get("password"));
    }
}