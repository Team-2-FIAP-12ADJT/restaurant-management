package com.fiap.restaurant_management.services;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.fiap.restaurant_management.repositories.AddressRepository;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.mappers.AddressMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.List;

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

    public List<AddressResponseDTO> findByUserId(UUID userId) {
        Users existingUser = this.usersRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));

        List<Address> addresses = this.addressRepository.findByUserIdAndDeletedAtIsNull(existingUser.getId());

        log.info("Found {} addresses for user with id: {}", addresses.size(), userId);

        return addresses.stream()
                .map(this.addressMapper::toAddressResponseDTO)
                .toList();
    }

    public AddressResponseDTO update(UUID userId, UUID addressId, AddressUpdateRequestDTO dto) {
        Address existingAddress = this.addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Address not found for user with id: " + userId));

        if (dto.street() != null || dto.number() != null || dto.neighborhood() != null) {
            String street = dto.street() != null ? dto.street() : existingAddress.getStreet();
            String number = dto.number() != null ? dto.number() : existingAddress.getNumber();
            String neighborhood = dto.neighborhood() != null ? dto.neighborhood() : existingAddress.getNeighborhood();

            Address duplicateAddress = this.addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                    street, number, neighborhood);

            if (duplicateAddress != null && !duplicateAddress.getId().equals(addressId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Address already exists");
            }
        }

        this.addressMapper.updateEntity(dto, existingAddress);
        Address savedAddress = this.addressRepository.save(existingAddress);
        log.info("Address with id: {} updated for user with id: {}", addressId, userId);
        return this.addressMapper.toAddressResponseDTO(savedAddress);
    }

    public void delete(UUID addressId) {
        Address existingAddress = this.addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Address not found with id: " + addressId));

        existingAddress.softDelete();
        this.addressRepository.save(existingAddress);
        log.info("Address with id: {} marked as deleted", addressId);
    }

}
