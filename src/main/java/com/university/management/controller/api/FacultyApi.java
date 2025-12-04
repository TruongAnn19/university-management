package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.FacultyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/faculties")
@Tag(name = "Faculty Management", description = "API Quản lý Khoa/Viện")
public interface FacultyApi {
    @Operation(summary = "Thêm mới Khoa đào tạo", description = "Chỉ dành cho ADMIN")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createFaculty(@Valid @RequestBody FacultyRequest request);
}
