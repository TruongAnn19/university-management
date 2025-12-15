package com.university.management.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDetailsDTO {
    private String fullName;
    private String dob;
    private String studentCode;
    private String className;
    private Double gpa;
}
