package com.university.management.service;

import org.springframework.web.multipart.MultipartFile;

public interface SemesterService {
    void importSemesters(MultipartFile file);
}