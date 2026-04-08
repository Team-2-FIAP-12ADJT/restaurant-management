package com.fiap.restaurant_management.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleEnum role) {
        if (role == null) {
            return null;
        }
        return role.getCode();
    }

    @Override
    public RoleEnum convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        return RoleEnum.fromCode(code);
    }
}
