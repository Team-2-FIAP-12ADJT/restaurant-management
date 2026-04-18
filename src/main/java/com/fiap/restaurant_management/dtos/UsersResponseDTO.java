package com.fiap.restaurant_management.dtos;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UsersResponseDTO(
                UUID id,
                String name,
                String email,
                String login,
                String role,
                List<AddressResponseDTO> addresses) {

}
