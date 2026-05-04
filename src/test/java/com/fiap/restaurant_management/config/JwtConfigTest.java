package com.fiap.restaurant_management.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

class JwtConfigTest {

    private final JwtConfig config = new JwtConfig();

    @Test
    void shouldCreateSecretKeyFromValidBase64Secret() {
        String secret = Base64.getEncoder()
                .encodeToString("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8));

        SecretKey key = config.jwtSecretKey(secret);

        assertEquals("HmacSHA256", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length);
    }

    @Test
    void shouldCreateJwtEncoderAndDecoder() {
        String secret = Base64.getEncoder()
                .encodeToString("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8));
        SecretKey key = config.jwtSecretKey(secret);

        assertNotNull(config.jwtEncoder(key));
        assertNotNull(config.jwtDecoder(key));
    }

    @Test
    void shouldRejectInvalidBase64Secret() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> config.jwtSecretKey("not-base64-secret"));

        assertEquals("Invalid JWT secret key. It must be a valid Base64-encoded string.", exception.getMessage());
    }

    @Test
    void shouldRejectBase64SecretShorterThanThirtyTwoBytes() {
        String secret = Base64.getEncoder().encodeToString("too-short".getBytes(StandardCharsets.UTF_8));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> config.jwtSecretKey(secret));

        assertEquals("Invalid JWT secret key. It must be a valid Base64-encoded string.", exception.getMessage());
        assertEquals("JWT secret key must be at least 256 bits (32 bytes) long", exception.getCause().getMessage());
    }
}
