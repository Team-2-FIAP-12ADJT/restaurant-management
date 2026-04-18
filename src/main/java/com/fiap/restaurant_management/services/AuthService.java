package com.fiap.restaurant_management.services;

import com.fiap.restaurant_management.dtos.AuthResultDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.mappers.AuthMapper;
import com.fiap.restaurant_management.repositories.UsersRepository;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements AuthServiceContract {

    private final UsersRepository usersRepository;
    private final AuthMapper authMapper;

    @Override
    public UsersLoginResponseDTO login(UsersLoginRequestDTO loginRequestDTO) {
        Users user = usersRepository.findByLoginIgnoreCaseAndDeletedAtIsNull(loginRequestDTO.login())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid login or password"
                ));

        if (!user.getPassword().equals(loginRequestDTO.password())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid login or password"
            );
        }

        log.info("User authenticated successfully: {}", user.getLogin());

        AuthResultDTO authResultDTO = new AuthResultDTO(
                UUID.randomUUID().toString(),
                LocalDateTime.now().plusMinutes(15)
        );

        return authMapper.toLoginResponseDTO(authResultDTO);
    }
}
