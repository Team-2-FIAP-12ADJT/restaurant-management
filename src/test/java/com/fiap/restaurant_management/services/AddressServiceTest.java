package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.mappers.AddressMapper;
import com.fiap.restaurant_management.repositories.AddressRepository;
import com.fiap.restaurant_management.repositories.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    @Test
    void shouldCreateAddressSuccessfully() {
        UUID userId = UUID.randomUUID();
        AddressRequestDTO request = buildValidRequest();

        Users existingUser = new Users();
        existingUser.setId(userId);

        Address mappedAddress = new Address();
        Address savedAddress = new Address();
        savedAddress.setId(UUID.randomUUID());
        savedAddress.setStreet(request.street());
        savedAddress.setNumber(request.number());
        savedAddress.setNeighborhood(request.neighborhood());
        savedAddress.setCity(request.city());
        savedAddress.setState(request.state());
        savedAddress.setZipCode(request.zipCode());
        savedAddress.setCountry(request.country());
        savedAddress.setComplement(request.complement());
        savedAddress.setUser(existingUser);

        AddressResponseDTO expected = AddressResponseDTO.builder()
                .id(savedAddress.getId())
                .street(savedAddress.getStreet())
                .number(savedAddress.getNumber())
                .neighborhood(savedAddress.getNeighborhood())
                .city(savedAddress.getCity())
                .state(savedAddress.getState())
                .zipCode(savedAddress.getZipCode())
                .country(savedAddress.getCountry())
                .complement(savedAddress.getComplement())
                .user(existingUser)
                .build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                request.street(), request.number(), request.neighborhood())).thenReturn(null);
        when(addressMapper.toEntity(request)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(savedAddress);
        when(addressMapper.toAddressResponseDTO(savedAddress)).thenReturn(expected);

        AddressResponseDTO result = addressService.create(userId, request);

        assertEquals(expected, result);
        assertEquals(existingUser, mappedAddress.getUser());
        verify(addressRepository).save(mappedAddress);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotExists() {
        UUID userId = UUID.randomUUID();
        AddressRequestDTO request = buildValidRequest();

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> addressService.create(userId, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldThrowConflictWhenAddressAlreadyExists() {
        UUID userId = UUID.randomUUID();
        AddressRequestDTO request = buildValidRequest();

        Users existingUser = new Users();
        Address existingAddress = new Address();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                request.street(), request.number(), request.neighborhood())).thenReturn(existingAddress);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> addressService.create(userId, request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(addressMapper, never()).toEntity(any(AddressRequestDTO.class));
        verify(addressRepository, never()).save(any(Address.class));
        verify(addressMapper, never()).toAddressResponseDTO(any(Address.class));
    }

    private AddressRequestDTO buildValidRequest() {
        return new AddressRequestDTO(
                "Rua das Flores",
                "123",
                "Centro",
                "Sao Paulo",
                "SP",
                "01310-100",
                "Brazil",
                "Apto 4B"
        );
    }
}
