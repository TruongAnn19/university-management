package com.university.management.model.dto.requestDto;

import com.university.management.model.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "Username không được để trống")
        String username,
        @NotBlank(message = "Password không được để trống")
        String password,
        @NotNull(message = "Role không được để trống")
        Role role,
        String adminKey
) {
}
