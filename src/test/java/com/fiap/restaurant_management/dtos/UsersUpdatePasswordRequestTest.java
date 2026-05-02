package com.fiap.restaurant_management.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UsersUpdatePasswordRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldPassValidationWhenPasswordsAreValid() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "OldPass@123",
                "NewPass@123"
        );

        Set<ConstraintViolation<UsersUpdatePasswordRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenOldPasswordIsBlank() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "",
                "NewPass@123"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenOldPasswordIsTooShort() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "A@1a",
                "NewPass@123"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenOldPasswordDoesNotMatchPattern() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "password123",
                "NewPass@123"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNewPasswordIsBlank() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "OldPass@123",
                ""
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNewPasswordIsTooLong() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "OldPass@123",
                "VeryLongPassword@123456789"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenNewPasswordDoesNotMatchPattern() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "OldPass@123",
                "password"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldTrimOldPassword() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "  OldPass@123  ",
                "NewPass@123"
        );

        assertEquals("OldPass@123", dto.oldPassword());
    }

    @Test
    void shouldTrimNewPassword() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "OldPass@123",
                "  NewPass@123  "
        );

        assertEquals("NewPass@123", dto.newPassword());
    }

    @Test
    void shouldKeepNullValues() {
        var dto = new UsersUpdatePasswordRequestDTO(
                null,
                null
        );

        assertNull(dto.oldPassword());
        assertNull(dto.newPassword());
    }

    @Test
    void shouldFailWhenPasswordBecomesBlankAfterTrim() {
        var dto = new UsersUpdatePasswordRequestDTO(
                "        ",
                "NewPass@123"
        );

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}