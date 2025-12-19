package com.university.management.service;

import com.university.management.model.dto.ChangePasswordRequest;
import com.university.management.model.dto.requestDto.LoginRequest;
import com.university.management.model.dto.requestDto.RegisterRequest;
import com.university.management.model.dto.response.AuthResponse;
import com.university.management.model.entity.User;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(ChangePasswordRequest request);
}
