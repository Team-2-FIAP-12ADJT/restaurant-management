package com.fiap.restaurant_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.enums.RoleEnum;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersServiceContract usersService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateUser() throws Exception {
        UsersRequestDTO request = buildValidUsersRequestDTO();
        UsersResponseDTO response = buildUsersResponseDTO();

        when(usersService.create(any())).thenReturn(response);

        mockMvc.perform(post(ApiPaths.V1_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(usersService).create(any());
    }

    @Test
    void shouldReturnBadRequestWhenCreateInvalid() throws Exception {
        // DTO vazio
        UsersRequestDTO request = buildInvalidUsersRequestDTO();

        mockMvc.perform(post(ApiPaths.V1_USERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUsersPage() throws Exception {
        UsersResponseDTO user = buildUsersResponseDTO();

        Page<UsersResponseDTO> page = new PageImpl<>(
                List.of(user),
                PageRequest.of(0, 10),
                1
        );

        when(usersService.findUsers(any(), any())).thenReturn(page);

        mockMvc.perform(get(ApiPaths.V1_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(1)) // você soma +1 no controller
                .andExpect(jsonPath("$.size").value(10));

        verify(usersService).findUsers(any(), any());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UsersRequestDTO request = buildValidUsersRequestDTO();
        UsersResponseDTO response = buildUsersResponseDTO();

        when(usersService.update(eq(userId), any())).thenReturn(response);

        mockMvc.perform(patch(ApiPaths.V1_USERS + "/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(usersService).update(eq(userId), any());
    }

    @Test
    void shouldReturnBadRequestWhenUpdateInvalid() throws Exception {
        UUID userId = UUID.randomUUID();

        UsersUpdateRequestDTO request = buildInvClientUpdateReques();

        mockMvc.perform(patch(ApiPaths.V1_USERS + "/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUserById() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UsersResponseDTO response = buildUsersResponseDTO();

        when(usersService.findById(userId)).thenReturn(response);

        mockMvc.perform(get(ApiPaths.V1_USERS + "/{userId}", userId))
                .andExpect(status().isOk());

        verify(usersService).findById(userId);
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(usersService).delete(userId);
    }

    @Test
    void shouldUpdatePasswordSuccessfully() throws Exception {
        UUID userId = UUID.randomUUID();

        UsersUpdatePasswordRequestDTO request =
                new UsersUpdatePasswordRequestDTO("Old@1234", "New@1234");

        mockMvc.perform(patch("/users/{userId}/password", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(usersService).updatePassword(
                eq(userId),
                any(UsersUpdatePasswordRequestDTO.class)
        );
    }

    private UsersRequestDTO buildValidUsersRequestDTO() {
        return new UsersRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                RoleEnum.OWNER,
                List.of(buildValidAddressRequest())
        );
    }

    private UsersRequestDTO buildInvalidUsersRequestDTO() {
        return new UsersRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                RoleEnum.OWNER,
                List.of(buildInvalidAddressRequest())
        );
    }

    private UsersResponseDTO buildUsersResponseDTO() {
        return new UsersResponseDTO(
                UUID.randomUUID(),
                "Gustavo",
                "gustavo@email.com",
                "gustavo123",
                "CLIENT",
                List.of(buildAddressResponseDTO())
        );
    }

    private AddressRequestDTO buildValidAddressRequest() {
        return new AddressRequestDTO(
                "Street",
                "123",
                "Neighborhood",
                "City",
                "SP",
                "00000-000",
                "Country",
                ""
        );
    }

    private AddressRequestDTO buildInvalidAddressRequest() {
        return new AddressRequestDTO(
                "Street",
                "123",
                "Neighborhood",
                "City",
                "state",
                "00000-000",
                "Country",
                ""
        );
    }

    private AddressResponseDTO buildAddressResponseDTO() {
        return new AddressResponseDTO(
                UUID.randomUUID(),
                "Street",
                "123",
                "Neighborhood",
                "City",
                "SP",
                "00000-000",
                "Country",
                ""
        );
    }

    private UsersUpdateRequestDTO buildInvClientUpdateReques(){
        return new UsersUpdateRequestDTO(
                "Gustavo",
                "",
                "gustavo@email.com",
                RoleEnum.CLIENT
        );
    }
}
