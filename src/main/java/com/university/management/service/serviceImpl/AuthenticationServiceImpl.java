package com.university.management.service.serviceImpl;

import com.university.management.model.dto.ChangePasswordRequest;
import com.university.management.model.dto.requestDto.LoginRequest;
import com.university.management.model.dto.requestDto.RegisterRequest;
import com.university.management.model.dto.response.AuthResponse;
import com.university.management.model.entity.Role;
import com.university.management.model.entity.User;
import com.university.management.repository.UserRepository;
import com.university.management.service.AuthenticationService;
import com.university.management.service.CaptchaService;
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
    private final CaptchaService captchaService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.STUDENT || request.role() == Role.TEACHER) {
            throw new RuntimeException("Không thể tự đăng ký Sinh viên/Giảng viên. Vui lòng liên hệ Admin.");
        }

        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
        var jwtToken = jwtUtils.generateToken(user.getUsername(), String.valueOf(user.getRole()));
        return new AuthResponse(jwtToken, "Tạo Admin thành công");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        captchaService.verifyCaptcha(request.recaptchaToken());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var user = userRepository.findByUsername(request.username())
                .orElseThrow();

        var jwtToken = jwtUtils.generateToken(user.getUsername(),  String.valueOf(user.getRole()));

        return new AuthResponse(jwtToken, "Đăng nhập thành công");
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUsername = authentication.getName();

        var user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalStateException("Mật khẩu cũ không chính xác");
        }

        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new IllegalStateException("Mật khẩu xác nhận không trùng khớp");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
