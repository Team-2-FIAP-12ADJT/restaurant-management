package com.fiap.restaurant_management.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.net.URI;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("Erro de validação: {}", ex.getMessage());

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setDetail("Um ou mais campos estão inválidos");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        problemDetail.setProperty("errors", fieldErrors);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("Erro de validação de parâmetros: {}", ex.getMessage());

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setDetail("Um ou mais campos estão inválidos");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            result.getResolvableErrors().forEach(error -> {
                if (error instanceof FieldError fieldError) {
                    fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
                } else {
                    fieldErrors.put("global", error.getDefaultMessage());
                }
            });
        });

        problemDetail.setProperty("errors", fieldErrors);
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Erro não tratado: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor");

        problemDetail.setTitle("Erro interno no servidor");
        problemDetail.setType(URI.create("https://api.locatech.com/errors/internal-server-error"));

        return problemDetail;
    }
}
