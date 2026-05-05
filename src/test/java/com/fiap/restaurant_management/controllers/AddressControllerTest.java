package com.fiap.restaurant_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.AddressResponseDTO;
import com.fiap.restaurant_management.services.interfaces.AddressServiceContract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AddressController.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressServiceContract addressService;

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
