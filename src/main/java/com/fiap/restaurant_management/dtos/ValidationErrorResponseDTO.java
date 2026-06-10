package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ValidationErrorResponseDTO(
        @Schema(example = "about:blank") String type,
        @Schema(example = "Bad Request") String title,
        @Schema(example = "400") int status,
        @Schema(example = "Request validation failed") String detail,
        @Schema(example = "/api/v1/users") String instance,
        @Schema(example = "{\"name\": \"Name is required\", \"email\": \"must be a well-formed email address\"}") Map<String, String> errors
) {}
