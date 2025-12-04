package com.university.management.model.dto.response;

public record AuthResponse(
        String token,
        String message
) {
}
