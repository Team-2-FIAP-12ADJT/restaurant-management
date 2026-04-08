package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record UsersUpdateRequestDTO(
        @NotBlank(message = "Password is required") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Password must contain upper, lower, number and special character") String password,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Login is required") String login,
        @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
        @NotNull(message = "Role is required") RoleEnum role,
        @NotNull(message = "ID is required") UUID userId,
        @Valid @NotNull(message = "Address details are required") AddressRequestDTO addresses) {

        public UsersUpdateRequestDTO {
                name = name != null ? name.trim() : null;
                login = login != null ? login.trim() : null;
                email = email != null ? email.trim() : null;
                password = password != null ? password.trim() : null;
        }
}
