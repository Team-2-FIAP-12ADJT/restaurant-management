package com.fiap.restaurant_management.dtos;

public record UsersFilterDTO(String name) {

    public UsersFilterDTO {
        name = name != null ? name.trim() : null;
    }
}
