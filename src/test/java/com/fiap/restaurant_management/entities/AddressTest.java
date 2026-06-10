package com.fiap.restaurant_management.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void shouldLinkAddressToUser() {
        Address address = new Address();
        Users user = new Users();

        address.linkToUser(user);

        assertEquals(user, address.getUser());
    }

    @Test
    void shouldSetDeletedAtWhenSoftDeleteIsCalled() {
        Address address = new Address();

        address.softDelete();

        assertNotNull(address.getDeletedAt());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Address address = new Address();
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Users user = new Users();

        address.setId(id);
        address.setStreet("Rua A");
        address.setNumber("123");
        address.setComplement("Apto 1");
        address.setNeighborhood("Centro");
        address.setCity("São Paulo");
        address.setState("SP");
        address.setZipCode("00000-000");
        address.setCountry("Brasil");
        address.setCreatedAt(now);
        address.setUpdatedAt(now);
        address.setDeletedAt(now);
        address.setUser(user);

        assertEquals(id, address.getId());
        assertEquals("Rua A", address.getStreet());
        assertEquals("123", address.getNumber());
        assertEquals("Apto 1", address.getComplement());
        assertEquals("Centro", address.getNeighborhood());
        assertEquals("São Paulo", address.getCity());
        assertEquals("SP", address.getState());
        assertEquals("00000-000", address.getZipCode());
        assertEquals("Brasil", address.getCountry());
        assertEquals(now, address.getCreatedAt());
        assertEquals(now, address.getUpdatedAt());
        assertEquals(now, address.getDeletedAt());
        assertEquals(user, address.getUser());
    }

    @Test
    void shouldUseAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Users user = new Users();

        Address address = new Address(
                id,
                "Rua B",
                "456",
                null,
                "Bairro",
                "Cidade",
                "Estado",
                "12345-678",
                "Brasil",
                now,
                now,
                null,
                user
        );

        assertEquals(id, address.getId());
        assertEquals("Rua B", address.getStreet());
        assertEquals("456", address.getNumber());
        assertEquals("Bairro", address.getNeighborhood());
        assertEquals("Cidade", address.getCity());
        assertEquals("Estado", address.getState());
        assertEquals("12345-678", address.getZipCode());
        assertEquals("Brasil", address.getCountry());
        assertEquals(user, address.getUser());
    }
}
