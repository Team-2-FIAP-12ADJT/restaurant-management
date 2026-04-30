package com.fiap.restaurant_management.dtos;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResourceErrorResponseTest {

    @Test
    void shouldCreateDtoWithAllFields() {
        UUID id = UUID.randomUUID();

        var dto = new ResourceErrorResponseDTO(
                "https://example.com/not-found",
                "Resource Not Found",
                404,
                "User not found",
                "/users/" + id,
                "User",
                id
        );

        assertEquals("https://example.com/not-found", dto.type());
        assertEquals("Resource Not Found", dto.title());
        assertEquals(404, dto.status());
        assertEquals("User not found", dto.detail());
        assertEquals("/users/" + id, dto.instance());
        assertEquals("User", dto.resourceName());
        assertEquals(id, dto.id());
    }

    @Test
    void shouldAllowNullFields() {
        var dto = new ResourceErrorResponseDTO(
                null,
                null,
                0,
                null,
                null,
                null,
                null
        );

        assertNull(dto.type());
        assertNull(dto.title());
        assertEquals(0, dto.status());
        assertNull(dto.detail());
        assertNull(dto.instance());
        assertNull(dto.resourceName());
        assertNull(dto.id());
    }

    @Test
    void shouldKeepSameIdReference() {
        UUID id = UUID.randomUUID();

        var dto = new ResourceErrorResponseDTO(
                "type",
                "title",
                400,
                "detail",
                "instance",
                "resource",
                id
        );

        assertEquals(id, dto.id());
    }
}