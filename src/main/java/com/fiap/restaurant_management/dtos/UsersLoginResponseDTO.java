package com.fiap.restaurant_management.dtos;

import java.time.LocalDateTime;

public record UsersLoginResponseDTO(
                String accessToken,
                LocalDateTime expiresAt) {
}