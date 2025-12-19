package com.university.management.model.dto;

import com.university.management.model.entity.StudentStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentDto {
    private String studentCode;
    private String fullName;
    private String className;
    private LocalDate dob;
    private StudentStatus status;
    private String facultyName;
}
