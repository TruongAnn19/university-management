package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.RegistrationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/registration")
@Tag(name = "Course Registration", description = "API Đăng ký tín chỉ")
public interface RegistrationApi {
    @Operation(summary = "Đăng ký lớp học phần")
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    ResponseEntity<String> register(@Valid @RequestBody RegistrationRequestDto request);
}
