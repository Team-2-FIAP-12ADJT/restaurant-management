package com.fiap.restaurant_management.exceptions;

import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private WebRequest request;

    @Test
    void shouldHandleMethodArgumentNotValid() throws NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objectName");

        bindingResult.addError(new FieldError("objectName", "name", "Nome é obrigatório"));
        bindingResult.addError(new FieldError("objectName", "email", "Email inválido"));

        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("dummyMethod", Object.class),
                0
        );

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                ex, headers, HttpStatus.BAD_REQUEST, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ProblemDetail body = (ProblemDetail) response.getBody();
        assertNotNull(body);
        assertEquals("Um ou mais campos estão inválidos", body.getDetail());

        assertNotNull(body.getProperties());
        Object errorsObj = body.getProperties().get("errors");

        Map<String, String> errors = new HashMap<>();

        if (errorsObj instanceof Map<?, ?> tempMap) {
            for (Map.Entry<?, ?> entry : tempMap.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    errors.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        assertEquals("Nome é obrigatório", errors.get("name"));
        assertEquals("Email inválido", errors.get("email"));
    }

    @Test
    void shouldHandleHandlerMethodValidationException() {
        HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        when(ex.getBody()).thenReturn(problemDetail);

        ParameterValidationResult result = mock(ParameterValidationResult.class);

        FieldError fieldError = new FieldError("obj", "age", "Idade inválida");

        when(result.getResolvableErrors()).thenReturn(List.of(fieldError));
        when(ex.getParameterValidationResults()).thenReturn(List.of(result));

        HttpHeaders headers = new HttpHeaders();


        ResponseEntity<Object> response = handler.handleHandlerMethodValidationException(
                ex, headers, HttpStatus.BAD_REQUEST, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ProblemDetail body = (ProblemDetail) response.getBody();
        assertNotNull(body);
        assertEquals("Um ou mais campos estão inválidos", body.getDetail());

        assertNotNull(body.getProperties());
        Object errorsObj = body.getProperties().get("errors");

        Map<String, String> errors = new HashMap<>();

        if (errorsObj instanceof Map<?, ?> tempMap) {
            for (Map.Entry<?, ?> entry : tempMap.entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    errors.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }

        assertEquals("Idade inválida", errors.get("age"));
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Erro qualquer");

        ProblemDetail response = handler.handleException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("Erro interno no servidor", response.getDetail());
        assertEquals("Erro interno no servidor", response.getTitle());
        assertEquals(
                URI.create("https://api.restaurant-management.com/errors/internal-server-error"),
                response.getType()
        );
    }

    @Test
    void shouldHandleGlobalErrorWhenNotFieldError() {
        HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        when(ex.getBody()).thenReturn(problemDetail);

        ObjectError objectError = new ObjectError("objectName", "erro global");

        ParameterValidationResult validationResult = mock(ParameterValidationResult.class);
        when(validationResult.getResolvableErrors()).thenReturn(List.of(objectError));

        when(ex.getParameterValidationResults()).thenReturn(List.of(validationResult));

        var handler = new GlobalExceptionHandler();

        ResponseEntity<Object> response = handler.handleHandlerMethodValidationException(
                ex, headers, status, request
        );

        assertNotNull(response);
        ProblemDetail body = (ProblemDetail) response.getBody();

        assertNotNull(body);
        assertEquals("Um ou mais campos estão inválidos", body.getDetail());
    }

    @SuppressWarnings("unused")
    private void dummyMethod(@Valid Object obj) {
    }

}
