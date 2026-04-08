package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.mappers.UsersMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

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
}
