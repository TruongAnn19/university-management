package com.university.management.service;

import org.springframework.web.multipart.MultipartFile;

public interface SubjectService {
    void importSubjects(MultipartFile file);
}
