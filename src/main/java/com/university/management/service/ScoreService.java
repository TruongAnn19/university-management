package com.university.management.service;

import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;

import java.util.List;

public interface ScoreService {
    ScoreDto recordScore(ScoreRequestDto request);
    List<ScoreDto> getStudentTranscript(String studentCode);
}
