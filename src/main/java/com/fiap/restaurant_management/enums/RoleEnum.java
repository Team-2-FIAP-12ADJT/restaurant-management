package com.fiap.restaurant_management.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleEnum {
    OWNER(1, "Owner"),
    CLIENT(2, "Client");

    private final Integer code;
    private final String description;

    RoleEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static RoleEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Código de Role inválido: " + code);
    }
}
