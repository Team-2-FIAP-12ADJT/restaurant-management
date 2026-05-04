package com.fiap.restaurant_management.dtos;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersUpdateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassWhenAllFieldsAreValid() {
        var dto = new UsersUpdateRequestDTO(
                "Gustavo",
                "gustavo123",
                "gustavo@email.com"
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassWhenAllFieldsAreNull() {
        var dto = new UsersUpdateRequestDTO(
                null,
                null,
                null
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        var dto = new UsersUpdateRequestDTO(
                "",
                "login123",
                "email@email.com"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginIsEmpty() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "",
                "email@email.com"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "login123",
                "invalid-email"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassWhenEmailIsNull() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "login123",
                null
        );

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldTrimName() {
        var dto = new UsersUpdateRequestDTO(
                "  Gustavo  ",
                "login123",
                "email@email.com"
        );

        assertEquals("Gustavo", dto.name());
    }

    @Test
    void shouldTrimLogin() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "  login123  ",
                "email@email.com"
        );

        assertEquals("login123", dto.login());
    }

    @Test
    void shouldTrimEmail() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "login123",
                "  email@email.com  "
        );

        assertEquals("email@email.com", dto.email());
    }

    @Test
    void shouldFailWhenNameBecomesEmptyAfterTrim() {
        var dto = new UsersUpdateRequestDTO(
                "   ",
                "login123",
                "email@email.com"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenLoginBecomesEmptyAfterTrim() {
        var dto = new UsersUpdateRequestDTO(
                "Name",
                "   ",
                "email@email.com"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
