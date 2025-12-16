package com.university.management.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TeacherDetailsDTO {
    private String fullName;
    private String teacherCode;
    private String degree;
    private String faculty;
}
