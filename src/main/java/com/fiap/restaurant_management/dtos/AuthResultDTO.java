package com.fiap.restaurant_management.dtos;

import java.time.LocalDateTime;

public record AuthResultDTO(
        String accessToken,
        LocalDateTime expiresAt
) {
}
