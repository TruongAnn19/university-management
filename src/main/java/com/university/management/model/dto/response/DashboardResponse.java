package com.university.management.model.dto.response;

import com.university.management.model.dto.StatPair;

import java.util.List;

public record DashboardResponse(
        long totalStudents,
        long totalTeachers,
        long totalFaculties,
        long totalSubjects,

        List<StatPair> studentsByFaculty, // Biểu đồ tròn
        List<StatPair> avgScoreBySubject
) {
}
