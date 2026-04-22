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

        when(repository.existsByLoginIgnoreCase("gustavo")).thenReturn(false);
        when(repository.existsByEmailIgnoreCase("email@test.com")).thenReturn(false);

        when(mapper.toEntity(dto)).thenReturn(user);
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
    void shouldSoftDeleteUser() {
        when(repository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        service.delete(userId);

        verify(repository).save(user);
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        UsersUpdatePasswordRequestDTO dto = mock(UsersUpdatePasswordRequestDTO.class);

        when(dto.oldPassword()).thenReturn("123");
        when(dto.newPassword()).thenReturn("456");

        user.setPassword("123");

        when(repository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(user));

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

        assertThrows(PasswordUpdateException.class,
                () -> service.updatePassword(userId, dto));
    }
}