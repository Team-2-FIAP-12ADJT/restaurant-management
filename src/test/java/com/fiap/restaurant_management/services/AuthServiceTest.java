package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.AuthResultDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.mappers.AuthMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository repository;

    @Mock
    private AuthMapper mapper;

    @InjectMocks
    private AuthService service;

    @Test
    void shouldLoginSuccessfully() {
        UsersLoginRequestDTO request = mock(UsersLoginRequestDTO.class);
        Users user = new Users();

        user.setLogin("gustavo");
        user.setPassword("123");

        when(request.login()).thenReturn("gustavo");
        when(request.password()).thenReturn("123");

        when(repository.findByLoginIgnoreCaseAndDeletedAtIsNull("gustavo"))
                .thenReturn(Optional.of(user));

        when(mapper.toLoginResponseDTO(any(AuthResultDTO.class)))
                .thenReturn(mock(UsersLoginResponseDTO.class));

        UsersLoginResponseDTO response = service.login(request);

        assertNotNull(response);
        verify(mapper).toLoginResponseDTO(any(AuthResultDTO.class));
    }

    @Test
    void shouldThrowUnauthorizedWhenUserNotFound() {
        UsersLoginRequestDTO request = mock(UsersLoginRequestDTO.class);

        when(request.login()).thenReturn("inexistente");

        when(repository.findByLoginIgnoreCaseAndDeletedAtIsNull("inexistente"))
                .thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordIsWrong() {
        UsersLoginRequestDTO request = mock(UsersLoginRequestDTO.class);
        Users user = new Users();

        user.setLogin("gustavo");
        user.setPassword("123");

        when(request.login()).thenReturn("gustavo");
        when(request.password()).thenReturn("errada");

        when(repository.findByLoginIgnoreCaseAndDeletedAtIsNull("gustavo"))
                .thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}