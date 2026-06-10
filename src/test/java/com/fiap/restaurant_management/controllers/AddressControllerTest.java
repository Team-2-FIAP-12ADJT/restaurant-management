package com.fiap.restaurant_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.restaurant_management.PostgreSQLIntegrationTestSupport;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.dtos.AddressUpdateRequestDTO;
import com.fiap.restaurant_management.security.UserSecurity;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest extends PostgreSQLIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressServiceContract addressService;

    @MockitoBean
    private UserSecurity userSecurity;

    @MockitoBean
    private AuthServiceContract authService;

    @MockitoBean
    private UsersServiceContract usersService;

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldCreateAddress_asOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        AddressRequestDTO request = buildValidRequest();
        AddressResponseDTO response = AddressResponseDTO.builder()
                .id(UUID.randomUUID())
                .street(request.street())
                .number(request.number())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .zipCode(request.zipCode())
                .country(request.country())
                .complement(request.complement())
                .build();

        when(addressService.create(eq(userId), any(AddressRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(ApiPaths.V1_ADDRESSES + "/{userId}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.street").value(response.street()));

        verify(addressService).create(eq(userId), any(AddressRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldReturn400WhenBodyInvalid() throws Exception {
        AddressRequestDTO invalidRequest = new AddressRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post(ApiPaths.V1_ADDRESSES + "/{userId}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }

    @Test
    void shouldReturn401WhenNoAuth() throws Exception {
        AddressRequestDTO request = buildValidRequest();

        mockMvc.perform(post(ApiPaths.V1_ADDRESSES + "/{userId}", UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldUpdateAddress_asOwner() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = buildValidUpdateRequest();
        AddressResponseDTO response = buildResponse(addressId, request);

        when(addressService.update(eq(userId), eq(addressId), any(AddressUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch(ApiPaths.V1_ADDRESSES + "/{userId}/{addressId}", userId, addressId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.street").value(request.street()));

        verify(addressService).update(eq(userId), eq(addressId), any(AddressUpdateRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldUpdateAddress_asClientSelf() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = buildValidUpdateRequest();
        AddressResponseDTO response = buildResponse(addressId, request);

        when(userSecurity.isSelf(any(), any())).thenReturn(true);
        when(addressService.update(eq(userId), eq(addressId), any(AddressUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch(ApiPaths.V1_ADDRESSES + "/{userId}/{addressId}", userId, addressId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.street").value(request.street()));

        verify(addressService).update(eq(userId), eq(addressId), any(AddressUpdateRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void shouldReturn403WhenClientUpdatesOtherUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        AddressUpdateRequestDTO request = buildValidUpdateRequest();

        when(userSecurity.isSelf(any(), any())).thenReturn(false);

        mockMvc.perform(patch(ApiPaths.V1_ADDRESSES + "/{userId}/{addressId}", userId, addressId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail")
                        .value("You can only access or modify your own user resource, unless you have OWNER role."));

        verifyNoInteractions(addressService);
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void shouldReturn400WhenUpdateBodyInvalid() throws Exception {
        AddressUpdateRequestDTO invalidRequest = new AddressUpdateRequestDTO(
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        mockMvc.perform(patch(ApiPaths.V1_ADDRESSES + "/{userId}/{addressId}", UUID.randomUUID(), UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(addressService);
    }

    @Test
    void shouldReturn401WhenNoAuthForUpdate() throws Exception {
        AddressUpdateRequestDTO request = buildValidUpdateRequest();

        mockMvc.perform(patch(ApiPaths.V1_ADDRESSES + "/{userId}/{addressId}", UUID.randomUUID(), UUID.randomUUID())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(addressService);
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

    private AddressUpdateRequestDTO buildValidUpdateRequest() {
        return new AddressUpdateRequestDTO(
                "Rua Nova",
                "456",
                "Apto 2",
                "Jardins",
                "Campinas",
                "SP",
                "13000-000",
                "Brazil"
        );
    }

    private AddressResponseDTO buildResponse(UUID addressId, AddressUpdateRequestDTO request) {
        return AddressResponseDTO.builder()
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
    }
}
