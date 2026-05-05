package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.entities.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddressMapperTest {

    private AddressMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(AddressMapper.class);
    }

    @Test
    void shouldMapRequestDtoToEntity() {
        AddressRequestDTO request = buildValidRequest();

        Address result = mapper.toEntity(request);

        assertNotNull(result);
        assertEquals(request.street(), result.getStreet());
        assertEquals(request.number(), result.getNumber());
        assertEquals(request.neighborhood(), result.getNeighborhood());
        assertEquals(request.city(), result.getCity());
        assertEquals(request.state(), result.getState());
        assertEquals(request.zipCode(), result.getZipCode());
        assertEquals(request.country(), result.getCountry());
        assertEquals(request.complement(), result.getComplement());
        assertNull(result.getId());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
        assertNull(result.getDeletedAt());
        assertNull(result.getUser());
    }

    @Test
    void shouldMapEntityToResponseDTO() {
        Users user = new Users();
        user.setId(UUID.randomUUID());
        user.setName("Ricardo");
        user.setLogin("ricardo");
        user.setEmail("ricardo@email.com");

        Address address = new Address();
        address.setId(UUID.randomUUID());
        address.setStreet("Rua das Flores");
        address.setNumber("123");
        address.setNeighborhood("Centro");
        address.setCity("Sao Paulo");
        address.setState("SP");
        address.setZipCode("01310-100");
        address.setCountry("Brazil");
        address.setComplement("Apto 4B");
        address.setUser(user);

        AddressResponseDTO response = mapper.toAddressResponseDTO(address);

        assertNotNull(response);
        assertEquals(address.getId(), response.id());
        assertEquals(address.getStreet(), response.street());
        assertEquals(address.getNumber(), response.number());
        assertEquals(address.getNeighborhood(), response.neighborhood());
        assertEquals(address.getCity(), response.city());
        assertEquals(address.getState(), response.state());
        assertEquals(address.getZipCode(), response.zipCode());
        assertEquals(address.getCountry(), response.country());
        assertEquals(address.getComplement(), response.complement());
        assertNotNull(response.user());
        assertEquals(user.getId(), response.user().getId());
        assertEquals(user.getLogin(), response.user().getLogin());
    }

    @Test
    void shouldHandleNullAddressResponseGracefully() {
        Address address = new Address();
        address.setStreet("Rua sem usuario");
        address.setUser(null);

        AddressResponseDTO response = mapper.toAddressResponseDTO(address);

        assertNotNull(response);
        assertEquals("Rua sem usuario", response.street());
        assertNull(response.user());
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
