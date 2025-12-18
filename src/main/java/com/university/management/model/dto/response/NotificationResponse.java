package com.university.management.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String title,
        String message,
        @JsonProperty("isRead")
        boolean isRead,
        LocalDateTime createdAt
) {
}
