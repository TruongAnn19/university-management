package com.university.management.controller.api;

import com.university.management.model.dto.ChangePasswordRequest;
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
    @Operation(summary = "Đăng ký tài khoản mới", description = "Đăng ký tài khoản")
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request);

    @Operation(summary = "Đăng nhập lấy Token", description = "Đăng nhập")
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request);

    @Operation(summary = "Đổi mật khẩu", description = "Yêu cầu người dùng phải đăng nhập trước (có Token)")
    @PostMapping("/change-password")
    ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request);
}
