package com.university.management.controller;

import com.university.management.controller.api.TeacherApi;
import com.university.management.model.dto.StudentDto;
import com.university.management.model.dto.requestDto.TeacherRequestDto;
import com.university.management.model.entity.Student;
import com.university.management.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeacherController implements TeacherApi {
    private final TeacherService teacherService;

    @Override
    public ResponseEntity<String> importTeachers(MultipartFile file) {
        try {
            teacherService.importTeachers(file);
            return ResponseEntity.ok("Import danh sách giảng viên thành công!");
        } catch (RuntimeException e) {
            // Trả về lỗi 400 Bad Request kèm thông báo lỗi chi tiết (ví dụ: Mã khoa không tồn tại)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> createTeacher(TeacherRequestDto request) {
        try {
            teacherService.createTeacher(request);
            return ResponseEntity.ok("Thêm giảng viên thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<StudentDto>> getListStudent(String facultyCode) {
        List<StudentDto> students = teacherService.getListStudent(facultyCode);
        return ResponseEntity.ok(students);
    }
}
