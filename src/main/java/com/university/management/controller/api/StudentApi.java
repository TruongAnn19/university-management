package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.StudentRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "API Quản lý sinh viên")
public interface StudentApi {
    @Operation(summary = "Import danh sách sinh viên từ Excel")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> importStudents(@RequestParam("file") MultipartFile file);

    @Operation(summary = "Thêm mới 1 sinh viên (Thủ công)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createStudent(@RequestBody @Valid StudentRequestDto request);
}
