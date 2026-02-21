package com.sdl.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
@Setter
public class JwtConfig {

    @NotBlank
    private String secret;

    @Positive
    private long expiration;

    private String issuer = "SecureDigitalLockerr";

    private byte[] secretKeyBytes;

    @PostConstruct
    public void init() {

        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }

        // If secret is Base64 encoded, decode it
        try {
            secretKeyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ex) {
            // If not Base64, use raw string bytes
            secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}