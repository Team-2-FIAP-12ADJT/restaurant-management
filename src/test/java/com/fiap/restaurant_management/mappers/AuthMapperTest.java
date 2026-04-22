package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthMapperTest {

    private UsersMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(UsersMapper.class);
    }

    @Test
    void shouldMapUserAndBindAddress_whenAddressIsPresent() {
        AddressRequestDTO adressdto = buildValidDTO();
        UsersRequestDTO dto = new UsersRequestDTO(
                "1234", "Gustavo","gusat","email@test.com", RoleEnum.OWNER, adressdto
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
        assertEquals("original@email.com", entity.getEmail()); // não sobrescreve
    }

    @Test
    void shouldIgnoreSensitiveAndImmutableFieldsOnUpdate() {
        Users entity = new Users();
        UUID originalId = UUID.randomUUID();

        entity.setId(originalId);
        entity.setPassword("senhaOriginal");

        UsersUpdateRequestDTO dto = new UsersUpdateRequestDTO(
                "Nome", "novo@email.com", "novaSenha",RoleEnum.CLIENT
        );

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(originalId, entity.getId()); // não altera
        assertEquals("senhaOriginal", entity.getPassword()); // não altera
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
        assertEquals(user, address.getUser()); // vínculo bidirecional
    }

    @Test
    void shouldMapAddressDtoToEntityWithoutUserReference() {
        AddressRequestDTO dto = buildValidDTO();

        Address address = mapper.addressDtoToEntity(dto);

        assertNotNull(address);
        assertNull(address.getUser()); // regra importante
        assertEquals("Rua X", address.getStreet());
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