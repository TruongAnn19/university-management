package com.university.management.controller;

import com.university.management.controller.api.ScoreApi;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.dto.response.TranscriptResponse;
import com.university.management.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScoreController implements ScoreApi {
    private final ScoreService scoreService;

    @Override
    public ResponseEntity<ScoreDto> createScore(ScoreRequestDto request) {
        return ResponseEntity.ok(scoreService.recordScore(request));
    }

    @Override
    public ResponseEntity<TranscriptResponse> getStudentScores(String studentCode) {
        return ResponseEntity.ok(scoreService.getStudentTranscript(studentCode));
    }

    @Override
    public ResponseEntity<TranscriptResponse> getMyTranscript() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(scoreService.getMyTranscriptByUsername(currentUsername));
    }

    @Override
    public ResponseEntity<String> importExcel(MultipartFile file) {
        scoreService.importScores(file);
        return ResponseEntity.ok("Import thành công!");
    }
}
