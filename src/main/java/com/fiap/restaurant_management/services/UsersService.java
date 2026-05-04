package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.UsersFilterDTO;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.dtos.UsersUpdatePasswordRequestDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.exceptions.PasswordUpdateException;
import com.fiap.restaurant_management.exceptions.ResourceNotFoundException;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UsersService implements UsersServiceContract {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, UsersMapper usersMapper, PasswordEncoder passwordEncoder) {
        this.usersRepository = Objects.requireNonNull(usersRepository);
        this.usersMapper = Objects.requireNonNull(usersMapper);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder);
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

        String hashedPassword = this.hashPassword(usersRequestDTO.password());
        usersMapped.setPassword(hashedPassword);

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

    public void delete(UUID userId) {
        Users user = this.usersRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        log.info("User found with id: {}", user.getId());
        user.softDelete();
        this.usersRepository.save(Objects.requireNonNull(user, "Mapped user cannot be null"));
    }

    public void updatePassword(UUID userId, UsersUpdatePasswordRequestDTO usersUpdatePasswordRequestDTO) {
        Users user = this.usersRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        log.info("User found with id: {}", user.getId());

        if (!passwordEncoder.matches(usersUpdatePasswordRequestDTO.oldPassword(), user.getPassword())) {
            throw new PasswordUpdateException(HttpStatus.BAD_REQUEST, "Old password is incorrect", userId);
        }

        if (passwordEncoder.matches(usersUpdatePasswordRequestDTO.newPassword(), user.getPassword())) {
            throw new PasswordUpdateException(HttpStatus.CONFLICT,
                    "New password must be different from the old password", userId);
        }
        String hashedPassword = this.hashPassword(usersUpdatePasswordRequestDTO.newPassword());
        user.setPassword(hashedPassword);
        this.usersRepository.save(user);
    }

    private String hashPassword(String password) {
        return this.passwordEncoder.encode(password);
    }

}
