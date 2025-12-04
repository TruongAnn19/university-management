package com.university.management.service.serviceImpl;

import com.university.management.model.dto.StatPair;
import com.university.management.model.dto.response.DashboardResponse;
import com.university.management.repository.*;
import com.university.management.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreRepository scoreRepository;

    @Override
    public DashboardResponse getDashboardData() {
        // 1. Lấy các số liệu tổng quan (Count)
        long totalStudents = studentRepository.count();
        long totalTeachers = teacherRepository.count();
        long totalFaculties = facultyRepository.count();
        long totalSubjects = subjectRepository.count();

        List<StatPair> studentsByFaculty = studentRepository.countStudentsByFaculty();
        List<StatPair> avgScoreBySubject = scoreRepository.getAvgScoreBySubject();

        return new DashboardResponse(
                totalStudents,
                totalTeachers,
                totalFaculties,
                totalSubjects,
                studentsByFaculty,
                avgScoreBySubject
        );
    }
}
