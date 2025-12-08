package com.university.management.service;

import org.springframework.web.multipart.MultipartFile;

public interface CourseClassService {
    void importClasses(MultipartFile file);
}
