package com.fiap.restaurant_management.dtos;

import java.util.UUID;

public record ResourceErrorResponseDTO(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String resourceName,
        UUID id
) {}