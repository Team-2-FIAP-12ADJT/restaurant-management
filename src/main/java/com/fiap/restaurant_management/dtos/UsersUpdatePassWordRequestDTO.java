package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsersUpdatePassWordRequestDTO(
        @NotBlank(message = "Old password must not be blank when provided") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "Old password must contain upper, lower, number and special character") String oldPassWord,
        @NotBlank(message = "New pasword must not be blank when provided") @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "New password must contain upper, lower, number and special character") String newPassWord
        ){
}