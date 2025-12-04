package com.university.management.controller;

import com.university.management.controller.api.StudentApi;
import com.university.management.model.dto.requestDto.StudentRequestDto;
import com.university.management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentApi {
    private final StudentService studentService;

    @Override
    public ResponseEntity<String> importStudents(MultipartFile file) {
        studentService.importStudents(file);
        return ResponseEntity.ok("Import sinh viên thành công!");
    }

    @Override
    public ResponseEntity<String> createStudent(StudentRequestDto request) {
        try {
            studentService.createStudent(request);
            return ResponseEntity.ok("Thêm sinh viên thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
