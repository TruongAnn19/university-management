package com.university.management.controller;

import com.university.management.model.dto.requestDto.LoginRequest;
import com.university.management.model.dto.requestDto.RegisterRequest;
import com.university.management.model.dto.response.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API Đăng nhập và Đăng ký")
public interface AuthApi {
    @Operation(summary = "Đăng ký tài khoản mới")
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request);

    @Operation(summary = "Đăng nhập lấy Token")
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request);
}
