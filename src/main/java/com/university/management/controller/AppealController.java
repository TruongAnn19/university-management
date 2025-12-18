package com.university.management.controller;

import com.university.management.controller.api.AppealApi;
import com.university.management.model.dto.requestDto.AppealRequest;
import com.university.management.model.dto.requestDto.AppealReviewRequest;
import com.university.management.model.dto.response.AppealResponse;
import com.university.management.model.entity.GradeAppeal;
import com.university.management.repository.StudentRepository;
import com.university.management.service.GradeAppealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppealController implements AppealApi {
    private final GradeAppealService appealService;
    private final StudentRepository studentRepository;

    @Override
    public ResponseEntity<String> createAppeal(AppealRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        var student = studentRepository.findByUser_Username(currentUsername).orElseThrow();

        appealService.createAppeal(student.getStudentCode(), request);
        return ResponseEntity.ok("Đã gửi yêu cầu phúc khảo!");
    }

    @Override
    public ResponseEntity<String> reviewAppeal(Long id, AppealReviewRequest request) {
        appealService.reviewAppeal(id, request);
        return ResponseEntity.ok("Đã xử lý phúc khảo.");
    }

    @Override
    public ResponseEntity<List<AppealResponse>> getPendingAppeals() {
        return ResponseEntity.ok(appealService.getPendingAppeals());
    }

    @Override
    public ResponseEntity<List<GradeAppeal>> getAppealsByStudent(String studentCode) {
        return ResponseEntity.ok(appealService.getAppealsByStudent(studentCode));
    }
}
