package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.repositories.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsersRepository usersRepository;

    @PostMapping("/login")
    public ResponseEntity<UsersLoginResponseDTO> login(@Valid @RequestBody UsersLoginRequestDTO request) {

        Users user = usersRepository
                .findByLoginIgnoreCaseAndDeletedAtIsNull(request.login())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid login or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid login or password");
        }

        UsersLoginResponseDTO response = new UsersLoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getRole().getDescription(),
                true
        );

        return ResponseEntity.ok(response);
    }

}
