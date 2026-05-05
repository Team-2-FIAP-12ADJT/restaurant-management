package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ResourceErrorResponseDTO(
        @Schema(example = "about:blank") String type,
        @Schema(example = "Unauthorized") String title,
        @Schema(example = "401") int status,
        @Schema(example = "Authentication required") String detail,
        @Schema(example = "/api/v1/users/e5cb7e35-0d07-4183-97d7-1e851c3ec236") String instance,
        @Schema(example = "Users") String resourceName,
        @Schema(example = "e5cb7e35-0d07-4183-97d7-1e851c3ec236") UUID id
) {}
