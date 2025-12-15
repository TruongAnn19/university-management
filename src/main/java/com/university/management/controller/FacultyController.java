package com.university.management.controller;

import com.university.management.controller.api.FacultyApi;
import com.university.management.model.dto.requestDto.FacultyRequest;
import com.university.management.model.entity.Faculty;
import com.university.management.repository.FacultyRepository;
import com.university.management.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FacultyController implements FacultyApi {
    private final FacultyService facultyService;
    private final FacultyRepository facultyRepository;

    @Override
    public ResponseEntity<String> createFaculty(FacultyRequest request) {
        try {
            facultyService.createFaculty(request);
            return ResponseEntity.ok("Thêm khoa mới thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return ResponseEntity.ok(facultyRepository.findAll());
    }
}
