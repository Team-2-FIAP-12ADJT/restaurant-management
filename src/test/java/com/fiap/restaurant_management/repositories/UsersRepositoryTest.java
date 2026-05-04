package com.fiap.restaurant_management.repositories;

import com.fiap.restaurant_management.PostgreSQLIntegrationTestSupport;
import com.fiap.restaurant_management.entities.Users;
import com.fiap.restaurant_management.enums.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest extends PostgreSQLIntegrationTestSupport {

    @Autowired
    private UsersRepository repository;

    private Users createUser(String name, String login, String email) {
        Users user = new Users();
        user.setName(name);
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword("123");
        user.setRole(RoleEnum.OWNER);

        user.setCreatedAt(LocalDateTime.now());

        return repository.save(user);
    }


    @Test
    @DisplayName("Deve retornar true quando login existir ignorando case")
    void shouldReturnTrue_whenLoginExistsIgnoringCase() {
        createUser("Gustavo", "gustavo", "g@email.com");

        boolean exists = repository.existsByLoginIgnoreCase("GUSTAVO");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalse_whenLoginDoesNotExist() {
        boolean exists = repository.existsByLoginIgnoreCase("inexistente");

        assertFalse(exists);
    }

    @Test
    void shouldReturnTrue_whenEmailExistsIgnoringCase() {
        createUser("Gustavo", "gustavo", "email@test.com");

        boolean exists = repository.existsByEmailIgnoreCase("EMAIL@test.com");

        assertTrue(exists);
    }


    @Test
    void shouldFindUser_whenNotDeleted() {
        Users user = createUser("Gustavo", "gustavo", "email@test.com");

        var result = repository.findByIdAndDeletedAtIsNull(user.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void shouldNotFindUser_whenDeleted() {
        Users user = createUser("Gustavo", "gustavo", "email@test.com");

        user.softDelete();
        repository.save(user);

        var result = repository.findByIdAndDeletedAtIsNull(user.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindUsersByNameIgnoringCase() {
        createUser("Gustavo Silva", "gustavo", "a@email.com");
        createUser("Maria", "maria", "b@email.com");

        var page = repository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                "GUSTAVO",
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
        assertEquals("Gustavo Silva", page.getContent().getFirst().getName());
    }

    @Test
    void shouldNotReturnDeletedUsersInNameSearch() {
        Users user = createUser("Gustavo Silva", "gustavo", "a@email.com");

        user.softDelete();
        repository.save(user);

        var page = repository.findByNameContainingIgnoreCaseAndDeletedAtIsNull(
                "gustavo",
                PageRequest.of(0, 10)
        );

        assertEquals(0, page.getTotalElements());
    }

    @Test
    void shouldReturnOnlyActiveUsers() {
        Users active = createUser("Ativo", "ativo", "ativo@email.com");

        Users deleted = createUser("Deletado", "del", "del@email.com");
        deleted.softDelete();
        repository.saveAndFlush(deleted);

        var page = repository.findByDeletedAtIsNull(
                PageRequest.of(0, 10)
        );

        assertTrue(page.getContent().stream().anyMatch(user -> user.getId().equals(active.getId())));
        assertFalse(page.getContent().stream().anyMatch(user -> user.getId().equals(deleted.getId())));
    }
}
