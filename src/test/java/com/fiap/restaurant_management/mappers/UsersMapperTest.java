package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsersMapperTest {

    private UsersMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new UsersMapperImpl();
    }

    @Test
    void shouldMapUserAndBindAddress_whenAddressIsPresent() {
        AddressRequestDTO adressdto = buildValidDTO();
        UsersRequestDTO dto = new UsersRequestDTO(
                "1234", "Gustavo","gusat","email@test.com", RoleEnum.OWNER, List.of(adressdto)
        );

        Users user = mapper.toEntity(dto);

        assertNotNull(user);
        assertNotNull(user.getAddresses());
        assertEquals(1, user.getAddresses().size());

        Address address = user.getAddresses().getFirst();
        assertEquals("Rua X", address.getStreet());
    }

    @Test
    void shouldUpdateOnlyNonNullFields() {
        Users entity = new Users();
        entity.setName("Original");
        entity.setEmail("original@email.com");

        UsersUpdateRequestDTO dto = new UsersUpdateRequestDTO(
                "Novo Nome", null, null,null
        );

        mapper.updateEntityFromDto(dto, entity);

        assertEquals("Novo Nome", entity.getName());
        assertEquals("original@email.com", entity.getEmail());
    }

    @Test
    void shouldIgnoreSensitiveAndImmutableFieldsOnUpdate() {
        Users entity = new Users();
        UUID originalId = UUID.randomUUID();

        entity.setId(originalId);
        entity.setPassword("senhaOriginal");

        UsersUpdateRequestDTO dto = new UsersUpdateRequestDTO(
                "Nome", "novoLogin", "novo@email.com", RoleEnum.CLIENT
        );

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(originalId, entity.getId());
        assertEquals("senhaOriginal", entity.getPassword());
    }

    @Test
    void shouldMapRoleDescriptionToResponse() {
        Users user = new Users();

        user.setRole(RoleEnum.CLIENT);

        UsersResponseDTO response = mapper.toResponseDTO(user);

        assertEquals("Client", response.role());
    }

    @Test
    void shouldAddAddressAndLinkUser() {
        Users user = new Users();
        Address address = new Address();

        user.addAddress(address);

        assertNotNull(user.getAddresses());
        assertEquals(1, user.getAddresses().size());
        assertEquals(user, address.getUser());
    }

    @Test
    void shouldMapAddressDtoToEntityWithoutUserReference() {
        AddressRequestDTO dto = buildValidDTO();

        Address address = mapper.addressDtoToEntity(dto);

        assertNotNull(address);
        assertNull(address.getUser());
        assertEquals("Rua X", address.getStreet());
    }

    @Test
    void shouldReturnNullWhenToEntityReceivesNull() {
        Users result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenToResponseDTOReceivesNull() {
        UsersResponseDTO result = mapper.toResponseDTO(null);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenAddressDtoToEntityReceivesNull() {
        Address result = mapper.addressDtoToEntity((AddressRequestDTO) null);

        assertNull(result);
    }

    @Test
    void shouldNotUpdateEntityWhenDtoIsNull() {
        Users entity = new Users();
        entity.setName("Original");

        mapper.updateEntityFromDto(null, entity);

        assertEquals("Original", entity.getName());
    }

    @Test
    void shouldMapAllBasicFieldsInToEntity() {
        UsersRequestDTO dto = new UsersRequestDTO(
                "Password@123",
                "Gustavo",
                "login123",
                "email@test.com",
                RoleEnum.CLIENT,
                List.of(buildValidDTO())
        );

        Users user = mapper.toEntity(dto);

        assertEquals("Gustavo", user.getName());
        assertEquals("login123", user.getLogin());
        assertEquals("email@test.com", user.getEmail());
        assertEquals("Password@123", user.getPassword());
        assertEquals(RoleEnum.CLIENT, user.getRole());
    }

    @Test
    void shouldMapAddressListToResponseDTO() {
        Users user = new Users();

        Address address = new Address();
        address.setStreet("Rua X");

        user.addAddress(address);

        UsersResponseDTO response = mapper.toResponseDTO(user);

        assertNotNull(response.addresses());
        assertEquals(1, response.addresses().size());
        assertEquals("Rua X", response.addresses().getFirst().street());
    }

    @Test
    void shouldReturnNullAddressListWhenUserHasNoAddresses() {
        Users user = new Users();

        UsersResponseDTO response = mapper.toResponseDTO(user);

        assertNull(response.addresses());
    }

    @Test
    void shouldReturnNullRoleDescriptionWhenRoleIsNull() {
        Users user = new Users();
        user.setRole(null);

        UsersResponseDTO response = mapper.toResponseDTO(user);

        assertNull(response.role());
    }

    @Test
    void shouldReturnEmptyListWhenInputIsEmpty() {
        List<Address> result = mapper.addressDtoToEntity(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotUpdateNameWhenNameIsNull() {
        Users entity = new Users();
        entity.setName("Original");

        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(dto.name()).thenReturn(null);
        when(dto.login()).thenReturn(null);
        when(dto.email()).thenReturn(null);
        when(dto.role()).thenReturn(null);

        mapper.updateEntityFromDto(dto, entity);

        assertEquals("Original", entity.getName());
    }
    @Test
    void shouldReturnNullWhenAddressIsNull() {
        AddressResponseDTO response = mapper.toAddressResponseDTO(null);

        assertNull(response);
    }

    @Test
    void shouldReturnNullWhenAddressDtoListIsNull() {
        List<Address> result = mapper.addressDtoToEntity((List<AddressRequestDTO>) null);

        assertNull(result);
    }

    @Test
    void shouldNotAddAddressWhenDtoAddressIsNull() {
        UsersRequestDTO dto = new UsersRequestDTO(
                "Password@123",
                "Gustavo",
                "login123",
                "email@test.com",
                RoleEnum.CLIENT,
                null
        );

        Users user = mapper.toEntity(dto);

        assertTrue(user.getAddresses() == null || user.getAddresses().isEmpty());
    }

    private AddressRequestDTO buildValidDTO() {
        return new AddressRequestDTO(
                "Rua X",
                "123",
                "Centro",
                "São Paulo",
                "SP",
                "12345-678",
                "Brasil",
                "Apto 10"
        );
    }
}
