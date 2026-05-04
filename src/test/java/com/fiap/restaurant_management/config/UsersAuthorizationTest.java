package com.fiap.restaurant_management.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.restaurant_management.PostgreSQLIntegrationTestSupport;
import com.fiap.restaurant_management.controllers.ApiPaths;
import com.fiap.restaurant_management.dtos.UsersUpdatePasswordRequestDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersFilterDTO;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;

@SpringBootTest
@AutoConfigureMockMvc
class UsersAuthorizationTest extends PostgreSQLIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsersServiceContract usersService;

    @Test
    void shouldReturnForbiddenWhenClientFindUsers() throws Exception {
        mockMvc.perform(get(ApiPaths.V1_USERS)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                .andExpect(status().isForbidden());

        verify(usersService, never()).findUsers(any(UsersFilterDTO.class), any(Pageable.class));
    }

    @Test
    void shouldReturnCurrentUserWhenClientRequestsMe() throws Exception {
        UUID userId = UUID.randomUUID();
        UsersResponseDTO response = new UsersResponseDTO(
                userId,
                "Client User",
                "client@example.com",
                "client",
                "CLIENT",
                List.of());

        when(usersService.findById(userId)).thenReturn(response);

        mockMvc.perform(get(ApiPaths.V1_USERS_ME)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(userId.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.login").value("client"))
                .andExpect(jsonPath("$.role").value("CLIENT"));

        verify(usersService).findById(userId);
    }

    @Test
    void shouldReturnUnauthorizedWhenRequestingMeWithoutToken() throws Exception {
        mockMvc.perform(get(ApiPaths.V1_USERS_ME))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(usersService);
    }

    @Test
    void shouldReturnForbiddenWhenClientUpdatesAnotherUser() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UsersUpdateRequestDTO request = new UsersUpdateRequestDTO(
                "Another Name",
                "another-login",
                "another@example.com");

        mockMvc.perform(patch(ApiPaths.V1_USERS + "/{userId}", anotherUserId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(authenticatedUserId.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail")
                        .value("You can only access or modify your own user resource, unless you have OWNER role."));

        verify(usersService, never()).update(any(UUID.class), any(UsersUpdateRequestDTO.class));
    }

    @Test
    void shouldAllowClientToUpdatesOwnPassword() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();
        UsersUpdatePasswordRequestDTO request = new UsersUpdatePasswordRequestDTO("Old@1234", "New@1234");

        mockMvc.perform(patch(ApiPaths.V1_USERS + "/{userId}/password", authenticatedUserId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(authenticatedUserId.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(usersService).updatePassword(authenticatedUserId, request);
    }

    @Test
    void shouldReturnForbiddenWhenClientUpdatesAnotherUsersPassword() throws Exception {
        UUID authenticatedUserId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID();
        UsersUpdatePasswordRequestDTO request = new UsersUpdatePasswordRequestDTO("Old@1234", "New@1234");

        mockMvc.perform(patch(ApiPaths.V1_USERS + "/{userId}/password", anotherUserId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(authenticatedUserId.toString()))
                                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail")
                        .value("You can only access or modify your own user resource, unless you have OWNER role."));

        verify(usersService, never()).updatePassword(any(UUID.class), any(UsersUpdatePasswordRequestDTO.class));
    }
}
