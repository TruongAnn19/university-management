package com.university.management.model.dto.response;

import com.university.management.model.dto.ScoreDto;

import java.util.List;

public record TranscriptResponse(
        String studentName,
        String facultyName,
        String status,
        String message,
        List<ScoreDto> scores
) { }
