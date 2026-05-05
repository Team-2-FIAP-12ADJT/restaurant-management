package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;

import java.util.UUID;

public interface AddressServiceContract {
    AddressResponseDTO create(UUID userId, AddressRequestDTO addressRequestDTO);
}
