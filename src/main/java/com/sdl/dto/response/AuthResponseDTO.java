package com.sdl.dto.response;

import java.time.LocalDateTime;

public class AuthResponseDTO {

    private final String accessToken;
    private final String tokenType;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final Long userId;
    private final String email;
    private final String role;
    private final String fullName;

    public AuthResponseDTO(String accessToken,
                           LocalDateTime issuedAt,
                           LocalDateTime expiresAt,
                           Long userId,
                           String email,
                           String role,
                           String fullName) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    // Getters

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }
}