package com.university.management.controller;

import com.university.management.controller.api.SubjectApi;
import com.university.management.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class SubjectController implements SubjectApi {

    private final SubjectService subjectService;

    @Override
    public ResponseEntity<String> importSubjects(MultipartFile file) {
        try {
            subjectService.importSubjects(file);
            return ResponseEntity.ok("Import danh sách môn học thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}