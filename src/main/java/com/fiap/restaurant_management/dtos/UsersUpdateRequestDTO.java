package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;
import jakarta.validation.constraints.*;

public record UsersUpdateRequestDTO(
                @Size(min = 1, message = "Name must not be blank when provided") String name,
                @Size(min = 1, message = "Login must not be blank when provided") String login,
                @Email(message = "Email should be valid") String email,
                RoleEnum role) {
        public UsersUpdateRequestDTO {
                name = name != null ? name.trim() : null;
                login = login != null ? login.trim() : null;
                email = email != null ? email.trim() : null;
        }
}