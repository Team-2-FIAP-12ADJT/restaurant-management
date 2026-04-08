package com.fiap.restaurant_management.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequestDTO(
                @NotBlank(message = "Street is required") String street,
                @NotBlank(message = "Number is required") String number,
                @NotBlank(message = "Neighborhood is required") String neighborhood,
                @NotBlank(message = "City is required") String city,
                @NotBlank(message = "State is required") @Pattern(regexp = "^[A-Z]{2}$", message = "State must be a valid two-letter abbreviation") String state,
                @NotBlank(message = "Zip code is required") @Pattern(regexp = "^\\d{5}-\\d{3}$", message = "Zip code must be in format 00000-000") String zipCode,
                @NotBlank(message = "Country is required") String country,
                String complement) {
}
