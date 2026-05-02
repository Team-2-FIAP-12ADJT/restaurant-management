package com.fiap.restaurant_management.dtos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersFilterTest {

    @Test
    void shouldTrimName() {
        var dto = new UsersFilterDTO("  Gustavo  ");

        assertEquals("Gustavo", dto.name());
    }

    @Test
    void shouldKeepNullName() {
        var dto = new UsersFilterDTO(null);

        assertNull(dto.name());
    }

    @Test
    void shouldConvertBlankSpacesToEmptyString() {
        var dto = new UsersFilterDTO("     ");

        assertEquals("", dto.name());
    }

    @Test
    void shouldKeepValidNameUnchanged() {
        var dto = new UsersFilterDTO("Gustavo");

        assertEquals("Gustavo", dto.name());
    }
}