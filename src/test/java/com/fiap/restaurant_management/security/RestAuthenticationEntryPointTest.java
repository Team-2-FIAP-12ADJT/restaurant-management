package com.fiap.restaurant_management.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class RestAuthenticationEntryPointTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestAuthenticationEntryPoint entryPoint = new RestAuthenticationEntryPoint(objectMapper);

    @Test
    void shouldReturnProblemDetailWhenAuthenticationFails() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Invalid token"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, response.getContentType());
        assertEquals("Unauthorized", body.get("title").asText());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), body.get("status").asInt());
        assertEquals("Authentication is required to access this resource.", body.get("detail").asText());
        assertEquals("/api/v1/users", body.get("instance").asText());
        assertEquals("https://api.restaurant-management.com/errors/unauthorized", body.get("type").asText());
    }
}
