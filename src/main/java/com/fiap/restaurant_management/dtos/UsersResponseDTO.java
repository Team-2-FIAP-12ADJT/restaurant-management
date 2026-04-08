package com.fiap.restaurant_management.dtos;

import java.util.List;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UsersResponseDTO(
                UUID id,
                String name,
                String email,
                String login,
                String role,
                List<AddressResponseDTO> addresses) {

}
