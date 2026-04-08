package com.fiap.restaurant_management.mappers;

import com.fiap.restaurant_management.dtos.*;
import com.fiap.restaurant_management.entities.Address; import com.fiap.restaurant_management.entities.Users; import org.mapstruct.*;


@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED) public interface UsersMapper { @Mapping(target = "id", ignore = true) @Mapping(target = "createdAt", ignore = true) @Mapping(target = "updatedAt", ignore = true) @Mapping(target = "deletedAt", ignore = true) @Mapping(target = "addresses", ignore = true) Users toEntity(UsersRequestDTO usersRequestDTO);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address addressDtoToEntity(AddressRequestDTO addressDto);

    @AfterMapping
    default void bindAddressAction(UsersRequestDTO dto, @MappingTarget Users user) {
        if (dto.address() != null) {
            Address mappedAddress = this.addressDtoToEntity(dto.address());
            user.addAddress(mappedAddress);
        }
    }

    @Mapping(target = "addresses", source = "addresses")
    @Mapping(target = "role", source = "role.description")
    UsersResponseDTO toResponseDTO(Users user);

    AddressResponseDTO toAddressResponseDTO(Address address);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "addresses", ignore = true)
    void update(Users source, @MappingTarget Users target);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "addresses", source = "addresses")
    void updateFromDto(UsersUpdateRequestDTO source, @MappingTarget Users target);

    @BeforeMapping
    default void clearAddressesOnUpdate(UsersUpdateRequestDTO source, @MappingTarget Users target) {
        if (source != null && source.addresses() != null && target.getAddresses() != null) {
            target.getAddresses().clear();
        }
    }
}