package com.university.management.model.dto.response;

import lombok.Data;

@Data
public class AppealResponse {
    private Long id;
    private String reason;
    private String status;
    private String studentCode;
    private String studentName;
    private String subjectName;
    private Double oldScore;
    private String teacherResponse;
}
