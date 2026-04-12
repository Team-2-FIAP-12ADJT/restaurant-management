package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.dtos.PageResponseDTO;
import com.fiap.restaurant_management.dtos.UsersFilterDTO;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.services.UsersService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<PageResponseDTO<UsersResponseDTO>> findUsers(
            UsersFilterDTO filter,
            Pageable pageable) {

        Page<UsersResponseDTO> usersPage = this.usersService.findUsers(filter, pageable);

        PageResponseDTO<UsersResponseDTO> response = new PageResponseDTO<>(
                usersPage.getContent(),
                usersPage.getNumber() + 1,
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<UsersResponseDTO> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO) {
        log.info("Updating user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.update(userId, updateRequestDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UsersResponseDTO> findById(@PathVariable UUID userId) {
        log.info("Finding user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.findById(userId));
    }

}
