package com.university.management.controller.apiImpl;

import com.university.management.controller.RegistrationApi;
import com.university.management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController implements RegistrationApi {
    private final RegistrationService registrationService;

    @Override
    public ResponseEntity<String> register(String studentCode, String classCode) {
        try {
            registrationService.registerCourse(studentCode, classCode);
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
