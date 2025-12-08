package com.university.management.controller;

import com.university.management.controller.api.CourseClassApi;
import com.university.management.service.CourseClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CourseClassController implements CourseClassApi {
    private final CourseClassService classService;

    @Override
    public ResponseEntity<String> importClasses(MultipartFile file) {
        try {
            classService.importClasses(file);
            return ResponseEntity.ok("Mở lớp học phần thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
