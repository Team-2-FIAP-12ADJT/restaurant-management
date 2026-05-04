package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;

public interface AuthServiceContract {
    UsersLoginResponseDTO login(UsersLoginRequestDTO loginRequestDTO);

    UsersResponseDTO register(RegisterRequestDTO registerRequestDTO);
}
