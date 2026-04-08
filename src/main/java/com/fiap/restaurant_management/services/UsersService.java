package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.mappers.UsersMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        checkLoginAndEmail(usersRequestDTO);
        Users usersMapped = this.usersMapper.toEntity(usersRequestDTO);
        Users user = this.usersRepository.save(Objects.requireNonNull(usersMapped, "Mapped user cannot be null"));
        log.info("User created with id: {}", user.getId());
        return this.usersMapper.toResponseDTO(user);
    }

        @Transactional
    public UsersResponseDTO update(@Valid UsersUpdateRequestDTO usersRequestDTO) {

        Users userFound = usersRepository.findById(usersRequestDTO.userId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if(!usersRequestDTO.login().contentEquals(userFound.getLogin())){
            checkLogin(usersRequestDTO.login());
        }
        if (!usersRequestDTO.email().contentEquals(userFound.getEmail())){
            checkEmail(usersRequestDTO.email());
        }
        Users toUpdate = new Users();
        this.usersMapper.update(userFound,toUpdate);
        this.usersMapper.updateFromDto(usersRequestDTO,toUpdate);
        Users user = this.usersRepository.save(Objects.requireNonNull(toUpdate, "Mapped user cannot be null"));
        return this.usersMapper.toResponseDTO(user);
    }

    public void checkLoginAndEmail(UsersRequestDTO usersRequestDTO){
        checkEmail(usersRequestDTO.email());
        checkLogin(usersRequestDTO.login());
    }

    private void checkLogin(String login) {
        if (this.usersRepository.existsByLoginIgnoreCase(login)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }
    }
    private void checkEmail(String email) {
        if (this.usersRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    public UsersResponseDTO findById(UUID userId) {
        Users userFound = usersRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        return this.usersMapper.toResponseDTO(userFound);
    }
}
