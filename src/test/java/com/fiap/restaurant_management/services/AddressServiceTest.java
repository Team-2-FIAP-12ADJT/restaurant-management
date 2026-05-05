package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;
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

    @Test
    void shouldUpdateAddressSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = new AddressUpdateRequestDTO(
                "Rua Nova",
                "456",
                "Apto 2",
                "Jardins",
                "Campinas",
                "SP",
                "13000-000",
                "Brazil");

        Address existingAddress = buildAddress(addressId, "Rua das Flores", "123", "Centro");
        Address savedAddress = buildAddress(addressId, request.street(), request.number(), request.neighborhood());
        savedAddress.setComplement(request.complement());
        savedAddress.setCity(request.city());
        savedAddress.setState(request.state());
        savedAddress.setZipCode(request.zipCode());
        savedAddress.setCountry(request.country());

        AddressResponseDTO expected = AddressResponseDTO.builder()
                .id(addressId)
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .country(request.country())
                .build();

        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId))
                .thenReturn(Optional.of(existingAddress));
        when(addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                request.street(), request.number(), request.neighborhood())).thenReturn(null);
        when(addressRepository.save(existingAddress)).thenReturn(savedAddress);
        when(addressMapper.toAddressResponseDTO(savedAddress)).thenReturn(expected);

        AddressResponseDTO result = addressService.update(userId, addressId, request);

        assertEquals(expected, result);
        verify(addressMapper).updateEntity(request, existingAddress);
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void shouldThrowNotFoundWhenAddressNotBelongsToUser() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = new AddressUpdateRequestDTO(
                "Rua Nova",
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> addressService.update(userId, addressId, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(addressMapper, never()).updateEntity(any(AddressUpdateRequestDTO.class), any(Address.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatedFieldsDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = new AddressUpdateRequestDTO(
                "Rua Nova",
                "456",
                null,
                "Jardins",
                null,
                null,
                null,
                null);

        Address existingAddress = buildAddress(addressId, "Rua das Flores", "123", "Centro");
        Address duplicateAddress = buildAddress(UUID.randomUUID(), request.street(), request.number(), request.neighborhood());

        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId))
                .thenReturn(Optional.of(existingAddress));
        when(addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                request.street(), request.number(), request.neighborhood())).thenReturn(duplicateAddress);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> addressService.update(userId, addressId, request));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(addressMapper, never()).updateEntity(any(AddressUpdateRequestDTO.class), any(Address.class));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void shouldNotThrowConflictWhenDuplicateIsSameAddress() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = new AddressUpdateRequestDTO(
                "Rua das Flores",
                "123",
                null,
                "Centro",
                null,
                null,
                null,
                null);

        Address existingAddress = buildAddress(addressId, "Rua das Flores", "123", "Centro");
        AddressResponseDTO expected = AddressResponseDTO.builder()
                .id(addressId)
                .street(existingAddress.getStreet())
                .number(existingAddress.getNumber())
                .neighborhood(existingAddress.getNeighborhood())
                .city(existingAddress.getCity())
                .state(existingAddress.getState())
                .zipCode(existingAddress.getZipCode())
                .country(existingAddress.getCountry())
                .build();

        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId))
                .thenReturn(Optional.of(existingAddress));
        when(addressRepository.findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                request.street(), request.number(), request.neighborhood())).thenReturn(existingAddress);
        when(addressRepository.save(existingAddress)).thenReturn(existingAddress);
        when(addressMapper.toAddressResponseDTO(existingAddress)).thenReturn(expected);

        AddressResponseDTO result = addressService.update(userId, addressId, request);

        assertEquals(expected, result);
        verify(addressMapper).updateEntity(request, existingAddress);
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void shouldNotCheckDuplicateWhenNoLocationFieldsProvided() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = new AddressUpdateRequestDTO(
                null,
                null,
                "Casa",
                null,
                null,
                null,
                null,
                null);

        Address existingAddress = buildAddress(addressId, "Rua das Flores", "123", "Centro");
        AddressResponseDTO expected = AddressResponseDTO.builder()
                .id(addressId)
                .street(existingAddress.getStreet())
                .number(existingAddress.getNumber())
                .complement("Casa")
                .neighborhood(existingAddress.getNeighborhood())
                .city(existingAddress.getCity())
                .state(existingAddress.getState())
                .zipCode(existingAddress.getZipCode())
                .country(existingAddress.getCountry())
                .build();

        when(addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId))
                .thenReturn(Optional.of(existingAddress));
        when(addressRepository.save(existingAddress)).thenReturn(existingAddress);
        when(addressMapper.toAddressResponseDTO(existingAddress)).thenReturn(expected);

        AddressResponseDTO result = addressService.update(userId, addressId, request);

        assertEquals(expected, result);
        verify(addressRepository, never()).findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(
                any(String.class), any(String.class), any(String.class));
        verify(addressMapper).updateEntity(request, existingAddress);
        verify(addressRepository).save(existingAddress);
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

    private Address buildAddress(UUID addressId, String street, String number, String neighborhood) {
        Address address = new Address();
        address.setId(addressId);
        address.setStreet(street);
        address.setNumber(number);
        address.setNeighborhood(neighborhood);
        address.setCity("Sao Paulo");
        address.setState("SP");
        address.setZipCode("01310-100");
        address.setCountry("Brazil");
        return address;
    }
}
