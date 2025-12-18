package com.university.management.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScoreDto {
    private Long id;
    private String studentCode;
    private String studentName;
    private String subjectName;
    private Integer credits;
    private Double processScore;
    private Double finalScore;
    private Double totalScore;
    private LocalDateTime updatedAt;
}
