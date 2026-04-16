package com.fiap.restaurant_management.services.interfaces;

import com.fiap.restaurant_management.dtos.UsersLoginRequestDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;

public interface AuthServiceContract {
    UsersLoginResponseDTO login(UsersLoginRequestDTO loginRequestDTO);
}
