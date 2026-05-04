package com.fiap.restaurant_management.entities;

import com.fiap.restaurant_management.enums.RoleEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsersTest {

    @Test
    void shouldAddAddressAndLinkUser() {
        Users user = new Users();
        Address address = new Address();

        user.addAddress(address);

        assertNotNull(user.getAddresses());
        assertEquals(1, user.getAddresses().size());
        assertEquals(user, address.getUser());
    }

    @Test
    void shouldInitializeAddressListWhenNull() {
        Users user = new Users();

        user.addAddress(new Address());

        assertNotNull(user.getAddresses());
    }

    @Test
    void shouldSoftDeleteUserAndAddresses() {
        Users user = new Users();
        Address address1 = new Address();
        Address address2 = new Address();

        user.addAddress(address1);
        user.addAddress(address2);

        user.softDelete();

        assertNotNull(user.getDeletedAt());
        assertEquals(user.getDeletedAt(), address1.getDeletedAt());
        assertEquals(user.getDeletedAt(), address2.getDeletedAt());
    }

    @Test
    void shouldSoftDeleteUserWithoutAddresses() {
        Users user = new Users();

        user.softDelete();

        assertNotNull(user.getDeletedAt());
    }

    @Test
    void shouldReturnTrueWhenEmailIsChanging() {
        Users user = new Users();
        user.setEmail("old@email.com");

        boolean result = user.isEmailChanging("new@email.com");

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEmailIsSameIgnoringCase() {
        Users user = new Users();
        user.setEmail("email@test.com");

        boolean result = user.isEmailChanging("EMAIL@test.com");

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueWhenLoginIsChanging() {
        Users user = new Users();
        user.setLogin("oldLogin");

        boolean result = user.isLoginChanging("newLogin");

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenLoginIsSameIgnoringCase() {
        Users user = new Users();
        user.setLogin("login");

        boolean result = user.isLoginChanging("LOGIN");

        assertFalse(result);
    }

    @Test
    void shouldMatchPasswordCorrectly() {
        Users user = new Users();
        user.setPassword("123");

        assertTrue(user.matchesPassword("123"));
        assertFalse(user.matchesPassword("456"));
    }

    @Test
    void shouldCheckPasswordEqualsCorrectly() {
        Users user = new Users();
        user.setPassword("abc");

        assertTrue(user.passwordEquals("abc"));
        assertFalse(user.passwordEquals("xyz"));
    }

    @Test
    void shouldUseAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Users user = new Users(
                id,
                "name",
                "login",
                "email@test.com",
                "password",
                RoleEnum.OWNER,
                now,
                now,
                null,
                null
        );

        assertEquals(id, user.getId());
        assertEquals("name", user.getName());
        assertEquals(RoleEnum.OWNER, user.getRole());
    }
}
