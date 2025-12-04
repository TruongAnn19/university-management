package com.university.management.model.dto.requestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FacultyRequest(
        @NotBlank(message = "Mã khoa không được để trống")
        String facultyCode,

        @NotBlank(message = "Tên khoa không được để trống")
        String facultyName,

        @NotNull(message = "Thời gian đào tạo không được null")
        @Min(value = 3, message = "Thời gian đào tạo tối thiểu 3 năm")
        Integer duration,

        @NotNull(message = "Tín chỉ yêu cầu không được null")
        @Min(value = 1, message = "Tín chỉ phải lớn hơn 0")
        Integer requiredCredits
) {
}
