package com.fiap.restaurant_management.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.validation.Valid;

import com.fiap.restaurant_management.controllers.interfaces.AddressControllerContract;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
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

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<AddressResponseDTO>> findByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(this.addressService.findByUserId(userId));
    }

    @Override
    @PreAuthorize("hasAnyRole('OWNER', 'CLIENT')")
    public ResponseEntity<List<AddressResponseDTO>> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(this.addressService.findByUserId(userId));
    }

    @Override
    @PreAuthorize("hasRole('OWNER') or @userSecurity.isSelf(#userId, authentication)")
    public ResponseEntity<AddressResponseDTO> update(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressUpdateRequestDTO dto) {
        return ResponseEntity.ok(this.addressService.update(userId, addressId, dto));
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable UUID addressId) {
        this.addressService.delete(addressId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
