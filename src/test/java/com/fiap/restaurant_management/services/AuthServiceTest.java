package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository repository;

    @Mock
    private UsersMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

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

        when(passwordEncoder.matches("123", "123")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn(mock(UsersLoginResponseDTO.class));

        UsersLoginResponseDTO response = service.login(request);

        assertNotNull(response);
        verify(tokenService).generateToken(user);
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
        when(passwordEncoder.matches("errada", "123")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void shouldRegisterClientSuccessfully() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                List.of());
        Users user = new Users();
        Users savedUser = new Users();
        UsersResponseDTO response = new UsersResponseDTO(
                UUID.randomUUID(),
                "Gustavo",
                "gustavo@email.com",
                "gustavo123",
                "CLIENT",
                List.of());

        when(repository.existsByLoginIgnoreCase("gustavo123")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("gustavo@email.com")).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode("Strong@123")).thenReturn("encoded-password");
        when(repository.save(user)).thenReturn(savedUser);
        when(mapper.toResponseDTO(savedUser)).thenReturn(response);

        UsersResponseDTO result = service.register(request);

        assertEquals(response, result);
        assertEquals(RoleEnum.CLIENT, user.getRole());
        assertEquals("encoded-password", user.getPassword());
        verify(repository).save(user);
    }

    @Test
    void shouldThrowConflictWhenRegisterLoginAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                List.of());

        when(repository.existsByLoginIgnoreCase("gustavo123")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowConflictWhenRegisterEmailAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO(
                "Strong@123",
                "Gustavo",
                "gustavo123",
                "gustavo@email.com",
                List.of());

        when(repository.existsByLoginIgnoreCase("gustavo123")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("gustavo@email.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(repository, never()).save(any());
    }
}
