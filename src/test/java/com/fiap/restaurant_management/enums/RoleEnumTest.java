package com.fiap.restaurant_management.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleEnumTest {

    @Test
    void shouldReturnCorrectCode() {
        assertEquals(1, RoleEnum.OWNER.getCode());
        assertEquals(2, RoleEnum.CLIENT.getCode());
    }

    @Test
    void shouldReturnCorrectDescription() {
        assertEquals("Owner", RoleEnum.OWNER.getDescription());
        assertEquals("Client", RoleEnum.CLIENT.getDescription());
    }

    @Test
    void shouldReturnRoleWhenCodeIsValid() {
        assertEquals(RoleEnum.OWNER, RoleEnum.fromCode(1));
        assertEquals(RoleEnum.CLIENT, RoleEnum.fromCode(2));
    }

    @Test
    void shouldReturnNullWhenCodeIsNull() {
        RoleEnum.fromCode(null);
        RoleEnum result = null;

        assertNull(result);
    }

    @Test
    void shouldThrowExceptionWhenCodeIsInvalid() {
        Integer invalidCode = 999;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RoleEnum.fromCode(invalidCode)
        );

        assertEquals("Código de Role inválido: 999", exception.getMessage());
    }

    @Test
    void shouldIterateAllValuesAndValidateConsistency() {
        for (RoleEnum role : RoleEnum.values()) {
            assertEquals(role, RoleEnum.fromCode(role.getCode()));
        }
    }
}