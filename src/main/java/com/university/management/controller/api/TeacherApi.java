package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.TeacherRequestDto;
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

@RequestMapping("/api/teachers")
@Tag(name = "Teacher Management", description = "API Quản lý giảng viên")
public interface TeacherApi {
    @Operation(summary = "Import giảng viên từ Excel",
            description = "Yêu cầu file có cột Mã Khoa khớp với DB. Chỉ ADMIN được dùng.")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')") // Chặn quyền: Chỉ Admin mới được tạo giảng viên
    ResponseEntity<String> importTeachers(@RequestParam("file") MultipartFile file);

    @Operation(summary = "Thêm mới 1 giảng viên (Thủ công)")
    @PostMapping // Đường dẫn gốc /api/teachers
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> createTeacher(@RequestBody @Valid TeacherRequestDto request);
}
