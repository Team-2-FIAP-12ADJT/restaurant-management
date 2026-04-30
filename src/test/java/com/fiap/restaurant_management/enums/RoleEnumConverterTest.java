package com.fiap.restaurant_management.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleEnumConverterTest {

    private final RoleEnumConverter converter = new RoleEnumConverter();

    @Test
    void shouldConvertRoleToDatabaseColumn() {
        Integer result = converter.convertToDatabaseColumn(RoleEnum.OWNER);

        assertEquals(1, result);
    }

    @Test
    void shouldReturnNullWhenConvertToDatabaseColumnWithNull() {
        Integer result = converter.convertToDatabaseColumn(null);

        assertNull(result);
    }

    @Test
    void shouldConvertCodeToEntityAttribute() {
        RoleEnum result = converter.convertToEntityAttribute(2);

        assertEquals(RoleEnum.CLIENT, result);
    }

    @Test
    void shouldReturnNullWhenConvertToEntityAttributeWithNull() {
        RoleEnum result = converter.convertToEntityAttribute(null);

        assertNull(result);
    }

    @Test
    void shouldThrowExceptionWhenConvertToEntityAttributeWithInvalidCode() {
        Integer invalidCode = 999;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute(invalidCode)
        );

        assertEquals("Código de Role inválido: 999", exception.getMessage());
    }

    @Test
    void shouldBeConsistentForAllEnumValues() {
        for (RoleEnum role : RoleEnum.values()) {
            Integer dbValue = converter.convertToDatabaseColumn(role);
            RoleEnum entityValue = converter.convertToEntityAttribute(dbValue);

            assertEquals(role, entityValue);
        }
    }
}