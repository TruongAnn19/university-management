package com.university.management.model.dto.response;

public record ClassResponse(
        String classCode,
        String subjectName,
        String facultyName,
        Integer credits,
        Integer currentSlot,
        Integer maxSlot
) {
}
