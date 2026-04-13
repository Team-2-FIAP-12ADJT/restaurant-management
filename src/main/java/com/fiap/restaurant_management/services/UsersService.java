package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.fiap.restaurant_management.exceptions.ResourceNotFoundException;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    public UsersService(UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersRepository = Objects.requireNonNull(usersRepository);
        this.usersMapper = Objects.requireNonNull(usersMapper);
    }

    @Transactional
    public UsersResponseDTO create(UsersRequestDTO usersRequestDTO) {
        if (this.usersRepository.existsByLoginIgnoreCase(usersRequestDTO.login())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }

        if (this.usersRepository.existsByEmailIgnoreCase(usersRequestDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        Users usersMapped = this.usersMapper.toEntity(usersRequestDTO);
        Users user = this.usersRepository.save(Objects.requireNonNull(usersMapped, "Mapped user cannot be null"));
        log.info("User created with id: {}", user.getId());
        return this.usersMapper.toResponseDTO(user);
    }

    public Page<UsersResponseDTO> findUsers(UsersFilterDTO filter, Pageable pageable) {
        String name = filter != null ? filter.name() : null;

        if (name != null && !name.isBlank()) {
            return this.usersRepository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(name, pageable)
                    .map(this.usersMapper::toResponseDTO);
        }

        return this.usersRepository.findByDeletedAtIsNull(pageable)
                .map(this.usersMapper::toResponseDTO);
    }

    public UsersResponseDTO update(UUID userId, UsersUpdateRequestDTO updateRequestDTO) {
        Users existingUser = this.usersRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (existingUser.isLoginChanging(updateRequestDTO.login())
                && this.usersRepository.existsByLoginIgnoreCase(updateRequestDTO.login())) {
            log.error("Login already exists");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }

        if (existingUser.isEmailChanging(updateRequestDTO.email())
                && this.usersRepository.existsByEmailIgnoreCase(updateRequestDTO.email())) {
            log.error("Email already exists");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        this.usersMapper.updateEntityFromDto(updateRequestDTO, existingUser);
        Users savedUser = this.usersRepository.save(Objects.requireNonNull(existingUser, "Mapped user cannot be null"));
        log.info("User updated with id: {}", savedUser.getId());
        return this.usersMapper.toResponseDTO(savedUser);
    }

    public UsersResponseDTO findById(UUID userId) {
        Users user = this.usersRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        log.info("User found with id: {}", user.getId());
        return this.usersMapper.toResponseDTO(user);
    }
}
