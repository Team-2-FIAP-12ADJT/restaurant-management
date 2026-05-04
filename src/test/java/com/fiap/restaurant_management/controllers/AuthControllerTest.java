package com.fiap.restaurant_management.controllers;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthServiceContract authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        UsersLoginRequestDTO request = new UsersLoginRequestDTO(
                "gustavo",
                "123456"
        );

        UsersLoginResponseDTO response = new UsersLoginResponseDTO(
                "fake-jwt-token",LocalDateTime.now()

        );

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post(ApiPaths.V1_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt-token"));
    }

    @Test
    void shouldReturnBadRequest_whenLoginIsInvalid() throws Exception {
        UsersLoginRequestDTO request = new UsersLoginRequestDTO(
                "",
                "123456"
        );

        mockMvc.perform(post(ApiPaths.V1_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenPasswordIsInvalid() throws Exception {
        UsersLoginRequestDTO request = new UsersLoginRequestDTO(
                "gustavo",
                ""
        );

        mockMvc.perform(post(ApiPaths.V1_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCallAuthService() throws Exception {
        UsersLoginRequestDTO request = new UsersLoginRequestDTO(
                "gustavo",
                "123456"
        );

        when(authService.login(any())).thenReturn(new UsersLoginResponseDTO("token",LocalDateTime.now()));

        mockMvc.perform(post(ApiPaths.V1_AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        org.mockito.Mockito.verify(authService)
                .login(any(UsersLoginRequestDTO.class));
    }

    @Test
    void shouldRegisterClientSuccessfully() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                List.of());
        UsersResponseDTO response = new UsersResponseDTO(
                UUID.randomUUID(),
                "Gustavo",
                "gustavo@email.com",
                "gustavo123",
                "CLIENT",
                List.of());

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post(ApiPaths.V1_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CLIENT"));

        org.mockito.Mockito.verify(authService)
                .register(any(RegisterRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterPayloadIsInvalid() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "weak",
                "",
                "",
                "invalid-email",
                List.of());

        mockMvc.perform(post(ApiPaths.V1_AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
