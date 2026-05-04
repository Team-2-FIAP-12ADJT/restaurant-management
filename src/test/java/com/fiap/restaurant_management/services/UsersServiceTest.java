package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.exceptions.PasswordUpdateException;
import com.fiap.restaurant_management.exceptions.ResourceNotFoundException;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository repository;

    @Mock
    private UsersMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersService service;

    private Users user;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        user = new Users();
        user.setId(userId);
        user.setLogin("gustavo");
        user.setEmail("email@test.com");
        user.setPassword("123");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UsersRequestDTO dto = mock(UsersRequestDTO.class);

        when(dto.login()).thenReturn("gustavo");
        when(dto.email()).thenReturn("email@test.com");
        when(dto.password()).thenReturn("raw-password");

        when(repository.existsByLoginIgnoreCase("gustavo")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("email@test.com")).thenReturn(false);

        when(mapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("raw-password")).thenReturn("hashed-password");
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDTO(user)).thenReturn(mock(UsersResponseDTO.class));

        UsersResponseDTO response = service.create(dto);

        assertNotNull(response);
        verify(repository).save(user);
    }

    @Test
    void shouldThrowConflictWhenLoginExists() {
        UsersRequestDTO dto = mock(UsersRequestDTO.class);

        when(dto.login()).thenReturn("gustavo");
        when(repository.existsByLoginIgnoreCase("gustavo")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(dto));
    }

    @Test
    void shouldThrowConflictWhenEmailExists() {
        UsersRequestDTO dto = mock(UsersRequestDTO.class);

        when(dto.login()).thenReturn("gustavo");
        when(dto.email()).thenReturn("email@test.com");

        when(repository.existsByLoginIgnoreCase("gustavo")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("email@test.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(dto));
    }

    @Test
    void shouldFindUsersByName() {
        Pageable pageable = PageRequest.of(0, 10);
        UsersFilterDTO filter = mock(UsersFilterDTO.class);

        when(filter.name()).thenReturn("gus");

        Page<Users> page = new PageImpl<>(List.of(user));

        when(repository.findByNameContainingIgnoreCaseAndDeletedAtIsNull("gus", pageable))
                .thenReturn(page);

        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        Page<UsersResponseDTO> result = service.findUsers(filter, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindAllActiveUsersWhenFilterIsNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Users> page = new PageImpl<>(List.of(user));

        when(repository.findByDeletedAtIsNull(pageable)).thenReturn(page);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        Page<UsersResponseDTO> result = service.findUsers(null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindUserById() {
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        when(mapper.toResponseDTO(user)).thenReturn(mock(UsersResponseDTO.class));

        UsersResponseDTO response = service.findById(userId);

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(userId));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(dto.login()).thenReturn("gustavo");
        when(dto.email()).thenReturn("email@test.com");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDTO(user)).thenReturn(mock(UsersResponseDTO.class));

        UsersResponseDTO response = service.update(userId, dto);

        assertNotNull(response);
        verify(mapper).updateEntityFromDto(dto, user);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingUser() {
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(userId, mock(UsersUpdateRequestDTO.class)));
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        UsersUpdatePasswordRequestDTO dto = mock(UsersUpdatePasswordRequestDTO.class);

        when(dto.oldPassword()).thenReturn("123");
        when(dto.newPassword()).thenReturn("456");

        user.setPassword("123");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "123")).thenReturn(true);
        when(passwordEncoder.matches("456", "123")).thenReturn(false);
        when(passwordEncoder.encode("456")).thenReturn("hashed-456");

        service.updatePassword(userId, dto);

        verify(repository).save(user);
    }

    @Test
    void shouldThrowWhenOldPasswordIsWrong() {
        UsersUpdatePasswordRequestDTO dto = mock(UsersUpdatePasswordRequestDTO.class);

        when(dto.oldPassword()).thenReturn("errada");
        user.setPassword("123");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "123")).thenReturn(false);

        assertThrows(PasswordUpdateException.class,
                () -> service.updatePassword(userId, dto));
    }

    @Test
    void shouldThrowWhenNewPasswordIsSame() {
        UsersUpdatePasswordRequestDTO dto = mock(UsersUpdatePasswordRequestDTO.class);

        when(dto.oldPassword()).thenReturn("123");
        when(dto.newPassword()).thenReturn("123");

        user.setPassword("123");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "123")).thenReturn(true);

        assertThrows(PasswordUpdateException.class,
                () -> service.updatePassword(userId, dto));
    }

    @Test
    void shouldFindAllUsersWhenNameIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);

        UsersFilterDTO filter = mock(UsersFilterDTO.class);
        when(filter.name()).thenReturn("   ");

        Page<Users> page = new PageImpl<>(List.of(user));

        when(repository.findByDeletedAtIsNull(pageable)).thenReturn(page);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        Page<UsersResponseDTO> result = service.findUsers(filter, pageable);

        assertEquals(1, result.getTotalElements());

        verify(repository).findByDeletedAtIsNull(pageable);
        verify(repository, never())
                .findByNameContainingIgnoreCaseAndDeletedAtIsNull(any(), any());
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.findById(userId));

        verify(repository).findByIdAndDeletedAtIsNull(userId);
    }

    @Test
    void shouldThrowConflictWhenLoginAlreadyExistsOnUpdate() {
        UUID userId = UUID.randomUUID();

        Users existingUser = mock(Users.class);
        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(existingUser));

        when(dto.login()).thenReturn("newLogin");
        when(existingUser.isLoginChanging("newLogin")).thenReturn(true);
        when(repository.existsByLoginIgnoreCase("newLogin")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.update(userId, dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Login already exists", ex.getReason());
    }

    @Test
    void shouldThrowConflictWhenEmailAlreadyExistsOnUpdate() {
        UUID userId = UUID.randomUUID();

        Users existingUser = mock(Users.class);
        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(existingUser));

        when(dto.email()).thenReturn("email@test.com");
        when(existingUser.isEmailChanging("email@test.com")).thenReturn(true);
        when(repository.existsByEmailIgnoreCase("email@test.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.update(userId, dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email already exists", ex.getReason());
    }

    @Test
    void shouldNotCheckLoginWhenLoginIsNotChanging() {
        Users user = mock(Users.class);

        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(dto.login()).thenReturn("sameLogin");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

        when(user.isLoginChanging("sameLogin")).thenReturn(false);

        when(repository.save(any())).thenReturn(user);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        service.update(userId, dto);

        verify(repository, never()).existsByLoginIgnoreCase(any());
    }

    @Test
    void shouldNotCheckEmailWhenEmailIsNotChanging() {
        Users user = mock(Users.class);

        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(dto.email()).thenReturn("same@email.com");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

        when(user.isEmailChanging("same@email.com")).thenReturn(false);

        when(repository.save(any())).thenReturn(user);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        service.update(userId, dto);

        verify(repository, never()).existsByEmailIgnoreCase(any());
    }

    @Test
    void shouldAllowUpdateWhenLoginChangesButDoesNotExist() {
        user.setLogin("oldLogin");

        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);
        when(dto.login()).thenReturn("newLogin");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

        when(repository.existsByLoginIgnoreCase("newLogin"))
                .thenReturn(false);

        when(repository.save(any())).thenReturn(user);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        service.update(userId, dto);

        verify(repository).existsByLoginIgnoreCase("newLogin");
    }

    @Test
    void shouldAllowUpdateWhenEmailChangesButDoesNotExist() {
        Users user = mock(Users.class);

        UsersUpdateRequestDTO dto = mock(UsersUpdateRequestDTO.class);

        when(dto.email()).thenReturn("new@email.com");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

        when(user.isEmailChanging("new@email.com")).thenReturn(true);
        when(repository.existsByEmailIgnoreCase("new@email.com")).thenReturn(false);

        when(repository.save(any())).thenReturn(user);
        when(mapper.toResponseDTO(any())).thenReturn(mock(UsersResponseDTO.class));

        service.update(userId, dto);

        verify(repository).existsByEmailIgnoreCase("new@email.com");
    }

    @Test
    void shouldSoftDeleteUser() {
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        service.delete(userId);

        verify(repository).save(user);
    }

    @Test
    void shouldThrowWhenUpdatingPasswordForNonExistingUser() {
        UsersUpdatePasswordRequestDTO dto = mock(UsersUpdatePasswordRequestDTO.class);

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updatePassword(userId, dto));
    }

    @Test
    void shouldThrowWhenDeletingNonExistingUser() {
        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(userId));
    }

}
