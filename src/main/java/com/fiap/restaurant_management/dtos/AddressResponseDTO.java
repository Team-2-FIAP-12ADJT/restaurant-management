package com.fiap.restaurant_management.dtos;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AddressResponseDTO(
                UUID id,
                String street,
                String number,
                String complement,
                String neighborhood,
                String city,
                String state,
                String zipCode,
                String country) {

}
