package com.university.management.model.dto.requestDto;

import jakarta.validation.constraints.NotBlank;

public record TeacherRequestDto(
        @NotBlank(message = "Username không được để trống")
        String username,

        @NotBlank(message = "Password không được để trống")
        String password,

        @NotBlank(message = "Mã giảng viên không được để trống")
        String teacherCode,

        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        String degree, // Thạc sĩ, Tiến sĩ

        @NotBlank(message = "Mã khoa không được để trống")
        String facultyCode // Ví dụ: CNTT
) {

}
