package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UsersLoginRequestDTO(
        @Schema(example = "joao123") @NotBlank(message = "Login is required") String login,
        @Schema(example = "Strong@123") @NotBlank(message = "Password is required") String password
) {
    public UsersLoginRequestDTO {
        login = login.trim();
        password = password.trim();
    }
}
