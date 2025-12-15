package com.university.management.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeacherDetailsDTO {
    private String fullName;
    private String teacherCode;
    private String degree;
    private String department;
}
