package com.university.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/registration")
@Tag(name = "Course Registration", description = "API Đăng ký tín chỉ")
public interface RegistrationApi {
    @Operation(summary = "Đăng ký lớp học phần")
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    ResponseEntity<String> register(@RequestParam("studentCode") String studentCode,
                                    @RequestParam("classCode") String classCode);
}
