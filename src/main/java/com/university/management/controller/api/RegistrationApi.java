package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.RegistrationRequestDto;
import com.university.management.model.dto.response.ClassResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/registration")
@Tag(name = "Course Registration", description = "API Đăng ký tín chỉ")
public interface RegistrationApi {

    @Operation(summary = "Đăng ký lớp học phần")
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    ResponseEntity<String> register(@Valid @RequestBody RegistrationRequestDto request);

    @Operation(summary = "Lấy danh sách lớp đang mở (Có thể lọc theo Khoa)")
    @GetMapping("/open-classes")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    ResponseEntity<List<ClassResponse>> getOpenClasses(
            @RequestParam(required = false) Long facultyId
    );

    @Operation(summary = "Hủy đăng ký lớp học phần")
    @PostMapping("/cancel") // Dùng POST cho an toàn và tiện gửi Body
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    ResponseEntity<String> cancelRegistration(@RequestBody RegistrationRequestDto request);
}
