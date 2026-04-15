package com.fiap.restaurant_management.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.util.UUID;

public class PasswordUpdateException extends ErrorResponseException {
    public PasswordUpdateException(HttpStatus status ,  String resourceName, UUID id) {
        super(status, createProblemDetail(status , resourceName, id), null);
    }

    private static ProblemDetail createProblemDetail(HttpStatus status ,String resourceName, UUID id) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status,
                resourceName + " usuário :" + id);
        problemDetail.setTitle("Dados incorretos");
        problemDetail.setType(URI.create("https://api.restaurant-management.com/errors/password-validate"));
        problemDetail.setProperty("resourceName", resourceName);
        problemDetail.setProperty("id", id);
        return problemDetail;
    }
}
