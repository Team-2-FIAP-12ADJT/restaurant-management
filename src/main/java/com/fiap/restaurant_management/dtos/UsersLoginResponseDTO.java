package com.fiap.restaurant_management.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersLoginResponseDTO(
        String accessToken,
        LocalDateTime expiresAt
) {
}