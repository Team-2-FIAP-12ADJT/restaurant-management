package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record RegisterRequestDTO(
        @Schema(example = "Strong@123") @NotBlank(message = "Password is required") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain upper, lower, number and special character") String password,
        @Schema(example = "João Silva") @NotBlank(message = "Name is required") String name,
        @Schema(example = "joao123") @NotBlank(message = "Login is required") String login,
        @Schema(example = "joao@example.com") @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
        @Valid List<AddressRequestDTO> address) {
    public RegisterRequestDTO {
        name = name != null ? name.trim() : null;
        login = login != null ? login.trim() : null;
        email = email != null ? email.trim().toLowerCase() : null;
        password = password != null ? password.trim() : null;
    }
}
