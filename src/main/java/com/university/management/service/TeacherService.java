package com.university.management.service;

import com.university.management.model.dto.StudentDto;
import com.university.management.model.dto.requestDto.TeacherRequestDto;
import com.university.management.model.entity.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherService {
    void importTeachers(MultipartFile file);
    void createTeacher(TeacherRequestDto request);
    List<StudentDto> getListStudent(String facultyCode);
}
