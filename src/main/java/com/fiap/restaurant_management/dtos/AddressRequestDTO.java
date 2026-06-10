package com.fiap.restaurant_management.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequestDTO(
        @Schema(example = "Rua das Flores") @NotBlank(message = "Street is required") String street,
        @Schema(example = "123") @NotBlank(message = "Number is required") String number,
        @Schema(example = "Centro") @NotBlank(message = "Neighborhood is required") String neighborhood,
        @Schema(example = "São Paulo") @NotBlank(message = "City is required") String city,
        @Schema(example = "SP") @NotBlank(message = "State is required") @Pattern(regexp = "^[A-Z]{2}$", message = "State must be a valid two-letter abbreviation") String state,
        @Schema(example = "01310-100") @NotBlank(message = "Zip code is required") @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Zip code must be in format 00000-000") String zipCode,
        @Schema(example = "Brazil") @NotBlank(message = "Country is required") String country,
        @Schema(example = "Apto 4B") String complement) {
}
