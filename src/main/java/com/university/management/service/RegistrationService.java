package com.university.management.service;


import com.university.management.model.dto.response.ClassResponse;

import java.util.List;

public interface RegistrationService {
    void registerCourse(String studentCode, String classCode);
    List<ClassResponse> getOpenClasses(Long facultyId);
}
