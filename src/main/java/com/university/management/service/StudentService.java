package com.university.management.service;

import com.university.management.model.dto.requestDto.StudentRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    void importStudents(MultipartFile file);
    void createStudent(StudentRequestDto request);
    void updateStudentGpa(String studentCode);
}
