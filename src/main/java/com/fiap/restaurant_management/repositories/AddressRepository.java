package com.fiap.restaurant_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fiap.restaurant_management.entities.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    Address findByStreetAndNumberAndNeighborhoodAndDeletedAtIsNull(String streetAddress, String number,
            String neighborhood);

    Optional<Address> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    List<Address> findByUserIdAndDeletedAtIsNull(UUID userId);
}
