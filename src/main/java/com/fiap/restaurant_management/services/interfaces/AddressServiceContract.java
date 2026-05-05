package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface AddressServiceContract {
    AddressResponseDTO create(UUID userId, AddressRequestDTO addressRequestDTO);

    List<AddressResponseDTO> findByUserId(UUID userId);

    AddressResponseDTO update(UUID userId, UUID addressId, AddressUpdateRequestDTO dto);

    void delete(UUID addressId);
}
