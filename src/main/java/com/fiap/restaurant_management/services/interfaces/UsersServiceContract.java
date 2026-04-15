package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.UsersFilterDTO;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UsersServiceContract {

    UsersResponseDTO create(UsersRequestDTO usersRequestDTO);

    Page<UsersResponseDTO> findUsers(UsersFilterDTO filter, Pageable pageable);

    UsersResponseDTO update(UUID userId, UsersUpdateRequestDTO updateRequestDTO);

    UsersResponseDTO findById(UUID userId);

    UsersLoginResponseDTO validateLogin(UsersLoginRequestDTO loginRequestDTO);
}