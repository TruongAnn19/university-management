package com.university.management.service.serviceImpl;

import com.university.management.model.dto.ChangePasswordRequest;
import com.university.management.model.dto.requestDto.LoginRequest;
import com.university.management.model.dto.requestDto.RegisterRequest;
import com.university.management.model.dto.response.AuthResponse;
import com.university.management.model.entity.User;
import com.university.management.repository.UserRepository;
import com.university.management.service.AuthenticationService;
import com.university.management.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.username()) // Record access: ko có get
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);

        var jwtToken = jwtUtils.generateToken(user.getUsername());

        // Record constructor mặc định (Canonical constructor)
        return new AuthResponse(jwtToken, "Đăng ký thành công");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow();

        var jwtToken = jwtUtils.generateToken(user.getUsername());

        return new AuthResponse(jwtToken, "Đăng nhập thành công");
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // 1. Lấy user đang đăng nhập từ Token (Security Context)
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUsername = authentication.getName();

        var user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng"));

        // 2. Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Mật khẩu cũ không chính xác");
        }

        // 3. Kiểm tra mật khẩu xác nhận
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Mật khẩu xác nhận không trùng khớp");
        }

        // 4. Mã hóa mật khẩu mới và lưu xuống DB
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
