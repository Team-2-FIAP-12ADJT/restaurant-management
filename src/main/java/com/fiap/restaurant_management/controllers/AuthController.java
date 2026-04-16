package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.services.interfaces.AuthServiceContract;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthServiceContract authService;

    public AuthController(AuthServiceContract authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @PostMapping("/login")
    public ResponseEntity<UsersLoginResponseDTO> login(
            @Valid @RequestBody UsersLoginRequestDTO usersLoginRequestDTO) {

        log.info("Login attempt for user: {}", usersLoginRequestDTO.login());

        return ResponseEntity.ok(authService.login(usersLoginRequestDTO));
    }
}
