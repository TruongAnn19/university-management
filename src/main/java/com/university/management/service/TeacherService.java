package com.university.management.service;

import com.university.management.model.dto.requestDto.TeacherRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface TeacherService {
    void importTeachers(MultipartFile file);
    void createTeacher(TeacherRequestDto request);
}
