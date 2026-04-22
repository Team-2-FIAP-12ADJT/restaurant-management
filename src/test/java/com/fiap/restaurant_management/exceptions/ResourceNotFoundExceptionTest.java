package com.fiap.restaurant_management.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateResourceNotFoundExceptionWithCorrectProblemDetail() {
        // Arrange
        String resourceName = "User";
        UUID id = UUID.randomUUID();

        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException(resourceName, id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());

        ProblemDetail problemDetail = ex.getBody();
        assertNotNull(problemDetail);

        // valida status
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());

        // valida detail
        assertEquals(
                resourceName + " com id " + id + " não encontrado",
                problemDetail.getDetail()
        );

        // valida title
        assertEquals("Recurso não encontrado", problemDetail.getTitle());

        // valida type
        assertEquals(
                URI.create("https://api.restaurant-management.com/errors/resource-not-found"),
                problemDetail.getType()
        );

        // valida propriedades customizadas
        assertNotNull(problemDetail.getProperties());
        assertEquals(resourceName, problemDetail.getProperties().get("resourceName"));
        assertEquals(id, problemDetail.getProperties().get("id"));
    }

    @Test
    void shouldHandleDifferentResourceNames() {
        String resourceName = "Order";
        UUID id = UUID.randomUUID();

        ResourceNotFoundException ex = new ResourceNotFoundException(resourceName, id);

        ProblemDetail body = ex.getBody();

        assertNotNull(body.getDetail());
        assertTrue(body.getDetail().contains("Order"));
        assertTrue(body.getDetail().contains(id.toString()));
    }
}
