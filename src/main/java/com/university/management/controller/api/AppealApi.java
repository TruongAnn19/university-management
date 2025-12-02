package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.AppealRequest;
import com.university.management.model.dto.requestDto.AppealReviewRequest;
import com.university.management.model.entity.GradeAppeal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/appeals")
@Tag(name = "Grade Appeals", description = "Quản lý phúc khảo")
public interface AppealApi {

    @Operation(summary = "Sinh viên gửi yêu cầu phúc khảo")
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    ResponseEntity<String> createAppeal(@Valid @RequestBody AppealRequest request);

    @Operation(summary = "Tìm đơn phúc khảo theo Mã Sinh Viên (Dành cho GV)")
    @GetMapping("/student/{studentCode}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')") // Chỉ GV/Admin được soi lịch sử của SV
    ResponseEntity<List<GradeAppeal>> getAppealsByStudent(@PathVariable String studentCode);

    @Operation(summary = "Giáo viên duyệt đơn phúc khảo")
    @PutMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    ResponseEntity<String> reviewAppeal(@PathVariable Long id, @RequestBody AppealReviewRequest request);

    @Operation(summary = "Xem danh sách đơn chờ duyệt (Dành cho GV)")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    ResponseEntity<List<GradeAppeal>> getPendingAppeals();
}
