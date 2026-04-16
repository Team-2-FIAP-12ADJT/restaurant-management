package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AuthResultDTO;
import com.fiap.restaurant_management.dtos.UsersLoginResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    UsersLoginResponseDTO toLoginResponseDTO(AuthResultDTO authResultDTO);
}
