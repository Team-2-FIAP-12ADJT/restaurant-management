package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsersServiceContract {

    UsersResponseDTO create(UsersRequestDTO usersRequestDTO);

    Page<UsersResponseDTO> findUsers(UsersFilterDTO filter, Pageable pageable);

    UsersResponseDTO update(UUID userId, UsersUpdateRequestDTO updateRequestDTO);

    UsersResponseDTO findById(UUID userId);

    void delete(UUID userId);

    void updatePassword(UUID userId, UsersUpdatePasswordRequestDTO usersUpdatePasswordRequestDTO);

}