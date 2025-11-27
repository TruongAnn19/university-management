package com.university.management.service;

import com.university.management.model.dto.requestDto.LoginRequest;
import com.university.management.model.dto.requestDto.RegisterRequest;
import com.university.management.model.dto.response.AuthResponse;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
