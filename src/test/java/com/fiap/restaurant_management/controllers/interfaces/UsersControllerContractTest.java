package com.fiap.restaurant_management.controllers.interfaces;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsersControllerContractTest {

    @Test
    void shouldDescribeUserIdPathParametersAsUuidStrings() throws NoSuchMethodException {
        assertUserIdSchema(UsersControllerContract.class.getMethod("update", UUID.class,
                com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO.class));
        assertUserIdSchema(UsersControllerContract.class.getMethod("findById", UUID.class));
        assertUserIdSchema(UsersControllerContract.class.getMethod("delete", UUID.class));
        assertUserIdSchema(UsersControllerContract.class.getMethod("updatePassword", UUID.class,
                com.fiap.restaurant_management.dtos.UsersUpdatePasswordRequestDTO.class));
    }

    private static void assertUserIdSchema(Method method) {
        Parameter parameter = method.getAnnotation(Operation.class).parameters()[0];

        assertEquals("userId", parameter.name());
        assertEquals("string", parameter.schema().type());
        assertEquals("uuid", parameter.schema().format());
    }
}
