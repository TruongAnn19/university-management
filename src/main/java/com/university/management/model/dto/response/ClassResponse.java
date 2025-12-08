package com.university.management.model.dto.response;

import java.time.LocalDate;

public record ClassResponse(
        String classCode,
        String subjectName,
        String facultyName,
        Integer credits,
        Integer currentSlot,
        Integer maxSlot,
        String studyStatus,
        boolean isRegistered,
        boolean isMyClass,
        String semesterName,
        LocalDate startDate,
        LocalDate endDate,
        String timeStatus
) {
}
