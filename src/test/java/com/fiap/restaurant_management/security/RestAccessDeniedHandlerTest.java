package com.fiap.restaurant_management.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class RestAccessDeniedHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestAccessDeniedHandler handler = new RestAccessDeniedHandler(objectMapper);

    @Test
    void shouldReturnProblemDetailWhenAccessIsDenied() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/admin");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Missing authority"));

        JsonNode body = objectMapper.readTree(response.getContentAsString());

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, response.getContentType());
        assertEquals("Forbidden", body.get("title").asText());
        assertEquals(HttpStatus.FORBIDDEN.value(), body.get("status").asInt());
        assertEquals("You do not have permission to access this resource.", body.get("detail").asText());
        assertEquals("/api/v1/admin", body.get("instance").asText());
        assertEquals("https://api.restaurant-management.com/errors/forbidden", body.get("type").asText());
    }
}
