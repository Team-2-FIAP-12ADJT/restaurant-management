package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.controllers.interfaces.AuthControllerContract;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class AuthController implements AuthControllerContract {

    private final AuthServiceContract authService;

    public AuthController(AuthServiceContract authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @Override
    public ResponseEntity<UsersLoginResponseDTO> login(
            @RequestBody UsersLoginRequestDTO usersLoginRequestDTO) {

        log.info("Login attempt for user: {}", usersLoginRequestDTO.login());

        return ResponseEntity.ok(authService.login(usersLoginRequestDTO));
    }
}
