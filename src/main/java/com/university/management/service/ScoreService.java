package com.university.management.service;

import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ScoreService {
    ScoreDto recordScore(ScoreRequestDto request);
    List<ScoreDto> getStudentTranscript(String studentCode);
    void importScores(MultipartFile file);
}
