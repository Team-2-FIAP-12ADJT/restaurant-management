package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequestDTO(
        @Schema(example = "Rua Nova") @Size(min = 1, max = 255) String street,
        @Schema(example = "456") @Size(min = 1, max = 50) String number,
        @Schema(example = "Apto 2") @Size(min = 1, max = 255) String complement,
        @Schema(example = "Jardins") @Size(min = 1, max = 255) String neighborhood,
        @Schema(example = "Sao Paulo") @Size(min = 1, max = 255) String city,
        @Schema(example = "SP") @Size(min = 2, max = 2) String state,
        @Schema(example = "01310-200") @Size(min = 1, max = 20) String zipCode,
        @Schema(example = "Brazil") @Size(min = 1, max = 100) String country) {
}
