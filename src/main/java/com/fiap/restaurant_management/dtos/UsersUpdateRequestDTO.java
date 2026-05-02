package com.fiap.restaurant_management.dtos;

import com.fiap.restaurant_management.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UsersUpdateRequestDTO(
                @Schema(example = "Gustavo") @Size(min = 1, message = "Name must not be blank when provided") String name,
                @Schema(example = "gustavo123") @Size(min = 1, message = "Login must not be blank when provided") String login,
                @Schema(example = "gustavo@email.com", format = "email") @Email(message = "Email should be valid") String email,
                @Schema(example = "2", description = "Código do papel do usuário. 1=Owner, 2=Client") RoleEnum role) {
        public UsersUpdateRequestDTO {
                name = name != null ? name.trim() : null;
                login = login != null ? login.trim() : null;
                email = email != null ? email.trim() : null;
        }
}
