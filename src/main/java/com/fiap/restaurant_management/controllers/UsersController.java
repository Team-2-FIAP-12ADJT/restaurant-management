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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

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
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UsersResponseDTO> create(@Valid @RequestBody UsersRequestDTO usersRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.usersService.create(usersRequestDTO));
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
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
    @PreAuthorize("hasAnyRole('OWNER', 'CLIENT')")
    public ResponseEntity<UsersResponseDTO> me(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        log.info("Finding user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.findById(userId));
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UsersResponseDTO> findById(@PathVariable UUID userId) {
        log.info("Finding user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.findById(userId));
    }

    @Override
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        log.info("Deleting user with id: {}", userId);
        this.usersService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PreAuthorize("hasRole('OWNER') or @userSecurity.isSelf(#userId, authentication)")
    public ResponseEntity<UsersResponseDTO> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UsersUpdateRequestDTO updateRequestDTO) {
        log.info("Updating user with id: {}", userId);
        return ResponseEntity.ok(this.usersService.update(userId, updateRequestDTO));
    }

    @Override
    @PreAuthorize("hasRole('OWNER') or @userSecurity.isSelf(#userId, authentication)")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID userId,
            @Valid @RequestBody UsersUpdatePasswordRequestDTO usersUpdatePasswordRequestDTO) {
        log.info("Update Pasword user with id: {}", userId);
        this.usersService.updatePassword(userId, usersUpdatePasswordRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
