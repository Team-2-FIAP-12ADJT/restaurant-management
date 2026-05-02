package com.fiap.restaurant_management.controllers;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ApiPathsTest {

    @Test
    void shouldHaveCorrectApiPathsConstants() {
        assertEquals("/api", ApiPaths.API_BASE);
        assertEquals("/api/v1", ApiPaths.V1);
        assertEquals("/api/v1/users", ApiPaths.V1_USERS);
        assertEquals("/api/v1/auth", ApiPaths.V1_AUTH);
        assertEquals("/api/v1/auth/login", ApiPaths.V1_AUTH_LOGIN);
    }

    @Test
    void shouldThrowExceptionWhenInstantiatingUtilityClass() throws Exception {
        Constructor<ApiPaths> constructor = ApiPaths.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        Throwable thrown = assertThrows(Throwable.class, constructor::newInstance);

        assertInstanceOf(InvocationTargetException.class, thrown);
        assertInstanceOf(UnsupportedOperationException.class, thrown.getCause());
    }
}