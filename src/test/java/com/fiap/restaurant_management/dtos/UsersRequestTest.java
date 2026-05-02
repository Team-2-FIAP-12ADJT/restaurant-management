package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UsersRequestTest {

    @Test
    void shouldTrimAndNormalizeFields() {
        UsersRequestDTO dto = new UsersRequestDTO(
                "  Password@123  ",
                "  Gustavo  ",
                "  login123  ",
                "  EMAIL@TEST.COM  ",
                RoleEnum.OWNER,
                List.of(mock(AddressRequestDTO.class))
        );

        assertEquals("Password@123", dto.password());
        assertEquals("Gustavo", dto.name());
        assertEquals("login123", dto.login());
        assertEquals("email@test.com", dto.email());
    }

    @Test
    void shouldHandleNullValues() {
        UsersRequestDTO dto = new UsersRequestDTO(
                null,
                null,
                null,
                null,
                RoleEnum.OWNER,
                List.of(mock(AddressRequestDTO.class))
        );

        assertNull(dto.password());
        assertNull(dto.name());
        assertNull(dto.login());
        assertNull(dto.email());
    }
}