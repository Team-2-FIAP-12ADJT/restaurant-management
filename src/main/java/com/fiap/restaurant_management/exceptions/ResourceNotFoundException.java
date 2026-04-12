package com.fiap.restaurant_management.exceptions;

import org.springframework.web.ErrorResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import java.net.URI;
import java.util.UUID;

public class ResourceNotFoundException extends ErrorResponseException {
    public ResourceNotFoundException(String resourceName, UUID id) {
        super(HttpStatus.NOT_FOUND, createProblemDetail(resourceName, id), null);
    }

    private static ProblemDetail createProblemDetail(String resourceName, UUID id) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                resourceName + " com id " + id + " não encontrado");
        problemDetail.setTitle("Recurso não encontrado");
        problemDetail.setType(URI.create("https://api.restaurant-management.com/errors/resource-not-found"));
        problemDetail.setProperty("resourceName", resourceName);
        problemDetail.setProperty("id", id);
        return problemDetail;
    }
}
