package com.fiap.restaurant_management.dtos;

import java.util.UUID;

public record UsersLoginResponseDTO(
        UUID id,
        String name,
        String email,
        String login,
        String role,
        boolean authenticated
) {
}