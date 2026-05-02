package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsersUpdatePasswordRequestDTO(
        @Schema(example = "Strong@123")
        @NotBlank(message = "Old password must not be blank when provided") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Old password must contain upper, lower, number and special character") String oldPassword,
        @Schema(example = "NewStrong@123")
        @NotBlank(message = "New pasword must not be blank when provided") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "New password must contain upper, lower, number and special character") String newPassword
        ){
        public UsersUpdatePasswordRequestDTO {
                oldPassword = oldPassword != null ? oldPassword.trim() : null;
                newPassword = newPassword != null ? newPassword.trim() : null;
        }
}
