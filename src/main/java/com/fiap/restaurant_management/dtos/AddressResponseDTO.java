package com.fiap.restaurant_management.dtos;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressResponseDTO(
                @Schema(example = "b1c2d3e4-f5a6-7890-abcd-ef1234567890") UUID id,
                @Schema(example = "Rua das Flores") String street,
                @Schema(example = "123") String number,
                @Schema(example = "Apto 4B") String complement,
                @Schema(example = "Centro") String neighborhood,
                @Schema(example = "São Paulo") String city,
                @Schema(example = "SP") String state,
                @Schema(example = "01310-100") String zipCode,
                @Schema(example = "Brazil") String country) {

}
