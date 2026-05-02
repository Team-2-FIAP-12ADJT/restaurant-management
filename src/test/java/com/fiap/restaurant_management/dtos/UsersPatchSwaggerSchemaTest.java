package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UsersPatchSwaggerSchemaTest {

    @Test
    void shouldExposeValidExamplesForUserUpdatePayload() {
        assertSchemaExample(UsersUpdateRequestDTO.class, "name", "Gustavo");
        assertSchemaExample(UsersUpdateRequestDTO.class, "login", "gustavo123");
        assertSchemaExample(UsersUpdateRequestDTO.class, "email", "gustavo@email.com");
        assertSchemaExample(UsersUpdateRequestDTO.class, "role", "2");
    }

    @Test
    void shouldExposeValidExamplesForPasswordUpdatePayload() {
        assertSchemaExample(UsersUpdatePasswordRequestDTO.class, "oldPassword", "Strong@123");
        assertSchemaExample(UsersUpdatePasswordRequestDTO.class, "newPassword", "NewStrong@123");
    }

    private static void assertSchemaExample(Class<?> recordType, String componentName, String expectedExample) {
        Constructor<?> canonicalConstructor = Arrays.stream(recordType.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == recordType.getRecordComponents().length)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canonical constructor not found for " + recordType.getSimpleName()));

        for (Parameter parameter : canonicalConstructor.getParameters()) {
            if (parameter.getName().equals(componentName)) {
                Schema schema = parameter.getAnnotation(Schema.class);
                assertNotNull(schema, () -> "Missing @Schema for parameter " + componentName);
                assertEquals(expectedExample, schema.example());
                return;
            }
        }
        throw new IllegalArgumentException("Constructor parameter not found: " + componentName);
    }
}
