package com.fiap.restaurant_management.dtos;

import jakarta.validation.constraints.NotBlank;

public record UsersLoginRequestDTO(
        @NotBlank(message = "Login is required") String login,
        @NotBlank(message = "Password is required") String password
) {
    public UsersLoginRequestDTO {
        login = login != null ? login.trim() : null;
        password = password != null ? password.trim() : null;
    }
}