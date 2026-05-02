package com.fiap.restaurant_management.controllers;

import com.fiap.restaurant_management.controllers.interfaces.UsersControllerContract;
import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.services.interfaces.UsersServiceContract;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
public class UsersController implements UsersControllerContract {

    private final UsersServiceContract usersService;

    public UsersController(UsersServiceContract usersService) {
        this.usersService = Objects.requireNonNull(usersService);
    }

    @Override
    public ResponseEntity<UsersResponseDTO> create(@Valid @RequestBody UsersRequestDTO usersRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usersService.create(usersRequestDTO));
    }

    @Override
    public ResponseEntity<PageResponseDTO<UsersResponseDTO>> findUsers(
            UsersFilterDTO filter,
            @Parameter(hidden = true) @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UsersResponseDTO> usersPage = this.usersService.findUsers(filter, pageable);

        PageResponseDTO<UsersResponseDTO> response = new PageResponseDTO<>(
                usersPage.getContent(),
                usersPage.getNumber() + 1,
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UsersResponseDTO> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO) {
        log.info("Updating user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.update(userId, updateRequestDTO));
    }

    @Override
    public ResponseEntity<UsersResponseDTO> findById(@PathVariable UUID userId) {
        log.info("Finding user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.findById(userId));
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        log.info("Deleting user with id: {}", userId);
        this.usersService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> updatePassWord(
            @PathVariable UUID userId,
            @Valid @RequestBody UsersUpdatePasswordRequestDTO usersUpdatePasswordRequestDTO) {
        log.info("Update Pasword user with id: {}", userId);
        this.usersService.updatePassword(userId, usersUpdatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
