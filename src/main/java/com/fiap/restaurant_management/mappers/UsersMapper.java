package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.AddressRequestDTO;
import com.fiap.restaurant_management.dtos.RegisterRequestDTO;
import com.fiap.restaurant_management.dtos.UsersRequestDTO;
import com.fiap.restaurant_management.dtos.UsersUpdateRequestDTO;
import com.fiap.restaurant_management.entities.Address;
import com.fiap.restaurant_management.entities.Users;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.fiap.restaurant_management.dtos.UsersResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Users toEntity(UsersRequestDTO usersRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Users toEntity(RegisterRequestDTO registerRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address addressDtoToEntity(AddressRequestDTO addressDto);

    List<Address> addressDtoToEntity(List<AddressRequestDTO> addressDto);

    @AfterMapping
    default void bindAddressAction(UsersRequestDTO dto, @MappingTarget Users user) {
        if (dto.address() != null) {
            List<Address> mappedAddresses = this.addressDtoToEntity(dto.address());
            mappedAddresses.forEach(user::addAddress);
        }
    }

    @AfterMapping
    default void bindAddressAction(RegisterRequestDTO dto, @MappingTarget Users user) {
        if (dto.address() != null) {
            List<Address> mappedAddresses = this.addressDtoToEntity(dto.address());
            mappedAddresses.forEach(user::addAddress);
        }
    }

    @Mapping(target = "addresses", source = "addresses")
    @Mapping(target = "role", source = "role.description")
    UsersResponseDTO toResponseDTO(Users user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    void updateEntityFromDto(UsersUpdateRequestDTO dto, @MappingTarget Users entity);

}
