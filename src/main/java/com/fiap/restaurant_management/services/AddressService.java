package com.fiap.restaurant_management.services;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.fiap.restaurant_management.repositories.AddressRepository;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.mappers.AddressMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Service
@Slf4j
public class AddressService implements AddressServiceContract {
    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, UsersRepository usersRepository,
            AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.usersRepository = usersRepository;
        this.addressMapper = addressMapper;
    }

    public AddressResponseDTO create(UUID userId,
            AddressRequestDTO addressRequestDTO) {
        Users existingUser = this.usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));
        Address existingAddress = this.addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                addressRequestDTO.street(),
                addressRequestDTO.number(),
                addressRequestDTO.neighborhood());

        if (existingAddress != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Address already exists");
        }

        log.info("Creating address for user with id: {}", userId);

        var addressEntity = this.addressMapper.toEntity(addressRequestDTO);
        addressEntity.setUser(existingUser);

        Address savedAddress = this.addressRepository.save(addressEntity);
        log.info("Address created with id: {} for user with id: {}", savedAddress.getId(), userId);
        return this.addressMapper.toAddressResponseDTO(savedAddress);
    }

}
