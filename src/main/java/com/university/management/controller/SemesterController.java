package com.university.management.controller;

import com.university.management.controller.api.SemesterApi;
import com.university.management.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class SemesterController implements SemesterApi {

    private final SemesterService semesterService;

    @Override
    public ResponseEntity<String> importSemesters(MultipartFile file) {
        try {
            semesterService.importSemesters(file);
            return ResponseEntity.ok("Import danh sách học kỳ thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}