package com.university.management.controller;

import com.university.management.controller.api.RegistrationApi;
import com.university.management.model.dto.requestDto.RegistrationRequestDto;
import com.university.management.model.dto.response.ClassResponse;
import com.university.management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegistrationController implements RegistrationApi {
    private final RegistrationService registrationService;

    @Override
    public ResponseEntity<String> register(RegistrationRequestDto request) {
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            registrationService.registerCourse(currentUsername, request.classCode());
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<ClassResponse>> getOpenClasses(Long facultyId) {
        return ResponseEntity.ok(registrationService.getOpenClasses(facultyId));
    }

    @Override
    public ResponseEntity<String> cancelRegistration(RegistrationRequestDto request) {
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            registrationService.cancelRegistration(currentUsername, request.classCode());
            return ResponseEntity.ok("Hủy đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
