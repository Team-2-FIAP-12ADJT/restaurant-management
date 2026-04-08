package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.services.UsersService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = Objects.requireNonNull(usersService);
    }

    @PostMapping
    public ResponseEntity<UsersResponseDTO> create(@Valid @RequestBody UsersRequestDTO usersRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usersService.create(usersRequestDTO));
    }

    @PutMapping
    public ResponseEntity<UsersResponseDTO> update(@Valid @RequestBody UsersUpdateRequestDTO usersRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usersService.update(usersRequestDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsersResponseDTO> findById(@PathVariable UUID userId) {
        return ResponseEntity.status(HttpStatus.OK).body(this.usersService.findById(userId));
    }

}
