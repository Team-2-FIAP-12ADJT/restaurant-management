package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UsersLoginResponseDTO(
                @Schema(example = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlNWNiN2UzNS0wZDA3LTQxODMtOTdkNy0xZTg1MWMzZWMyMzYiLCJyb2xlcyI6WyJDTElFTlQiXSwiaWF0IjoxNzQ2NjY4MDAwLCJleHAiOjE3NDY2NzE2MDB9.signature") String accessToken,
                @Schema(example = "2026-05-05T04:00:00") LocalDateTime expiresAt) {
}
