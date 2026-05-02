package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        for (RecordComponent component : recordType.getRecordComponents()) {
            if (component.getName().equals(componentName)) {
                assertEquals(expectedExample, component.getAnnotation(Schema.class).example());
                return;
            }
        }
        throw new IllegalArgumentException("Record component not found: " + componentName);
    }
}
