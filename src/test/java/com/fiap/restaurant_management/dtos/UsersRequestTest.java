package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AddressRequestDTO validAddress() {
        return new AddressRequestDTO(
                "Street",
                "123",
                "Neighborhood",
                "City",
                "AS",
                "00000-000",
                "ASSDF",
                "adf"
        );
    }

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenPasswordIsBlank() {
        var dto = new UsersRequestDTO(
                "",
                "Name",
                "login",
                "email@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenPasswordDoesNotMatchPattern() {
        var dto = new UsersRequestDTO(
                "password",
                "Name",
                "login",
                "email@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "",
                "login",
                "email@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "Name",
                "",
                "email@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "Name",
                "login",
                "invalid-email",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenRoleIsNull() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "Name",
                "login",
                "email@email.com",
                null,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenAddressIsNull() {
        var dto = new UsersRequestDTO(
                "Password@123",
                "Name",
                "login",
                "email@email.com",
                RoleEnum.CLIENT,
                null
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldTrimAndNormalizeFields() {
        var dto = new UsersRequestDTO(
                "  Password@123  ",
                "  Gustavo  ",
                "  login123  ",
                "  EMAIL@EMAIL.COM  ",
                RoleEnum.CLIENT,
                validAddress()
        );

        assertEquals("Password@123", dto.password());
        assertEquals("Gustavo", dto.name());
        assertEquals("login123", dto.login());
        assertEquals("email@email.com", dto.email());
    }

    @Test
    void shouldFailWhenPasswordBecomesBlankAfterTrim() {
        var dto = new UsersRequestDTO(
                "     ",
                "Name",
                "login",
                "email@email.com",
                RoleEnum.CLIENT,
                validAddress()
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldHandleNullValuesInConstructor() {
        var dto = new UsersRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertNull(dto.password());
        assertNull(dto.name());
        assertNull(dto.login());
        assertNull(dto.email());
    }
}