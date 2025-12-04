package com.university.management.model.dto;

import lombok.Data;

@Data
public class ScoreDto {
    private String studentCode;
    private String studentName;
    private String subjectName;
    private Integer credits;
    private Double processScore;
    private Double finalScore;
    private Double totalScore;
}
