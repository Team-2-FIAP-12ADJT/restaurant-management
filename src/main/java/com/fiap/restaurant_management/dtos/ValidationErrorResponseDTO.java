package com.fiap.restaurant_management.dtos;

import java.util.Map;

public record ValidationErrorResponseDTO(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Map<String, String> errors
) {}