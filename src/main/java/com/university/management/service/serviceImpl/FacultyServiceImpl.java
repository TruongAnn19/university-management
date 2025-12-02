package com.university.management.service.serviceImpl;

import com.university.management.model.dto.requestDto.FacultyRequest;
import com.university.management.model.entity.Faculty;
import com.university.management.repository.FacultyRepository;
import com.university.management.service.FacultyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    @Override
    @Transactional
    public void createFaculty(FacultyRequest request) {
        if (facultyRepository.findByFacultyCode(request.facultyCode()).isPresent()) {
            throw new RuntimeException("Mã khoa đã tồn tại: " + request.facultyCode());
        }

        Faculty faculty = Faculty.builder()
                .facultyCode(request.facultyCode())
                .facultyName(request.facultyName())
                .duration(request.duration())
                .requiredCredits(request.requiredCredits())
                .build();
        facultyRepository.save(faculty);
    }
}
