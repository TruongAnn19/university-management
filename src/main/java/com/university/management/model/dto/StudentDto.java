package com.university.management.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentDto {
    private String studentCode;
    private String fullName;
    private String className;
    private LocalDate dob;
}
