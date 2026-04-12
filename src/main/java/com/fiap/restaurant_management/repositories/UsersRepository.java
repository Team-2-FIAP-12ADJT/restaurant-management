package com.fiap.restaurant_management.repositories;

import com.fiap.restaurant_management.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

	boolean existsByLoginIgnoreCase(String login);

	boolean existsByEmailIgnoreCase(String email);

	Optional<Users> findByIdAndDeletedAtIsNull(UUID id);
}
