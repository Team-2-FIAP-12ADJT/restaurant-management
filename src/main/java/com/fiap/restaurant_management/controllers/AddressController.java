package com.fiap.restaurant_management.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

import com.fiap.restaurant_management.controllers.interfaces.AddressControllerContract;

import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@RestController
@Slf4j
public class AddressController implements AddressControllerContract {
    private final AddressServiceContract addressService;

    public AddressController(AddressServiceContract addressService) {
        this.addressService = addressService;
    }

    @Override
    @PreAuthorize("hasRole('OWNER') or @userSecurity.isSelf(#userId, authentication)")
    public ResponseEntity<AddressResponseDTO> create(
            @PathVariable UUID userId,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        var addressResponse = this.addressService.create(userId, addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }
}
