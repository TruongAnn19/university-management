package com.university.management.model.dto.requestDto;

import com.university.management.model.entity.Faculty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentRequestDto(
        @NotBlank(message = "Username không được để trống")
        String username,

        @NotBlank(message = "Password không được để trống")
        String password,

        @NotBlank(message = "Mã sinh viên không được để trống")
        String studentCode,

        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @NotBlank(message = "Lớp hành chính không được để trống")
        String className,

        @NotNull(message = "Ngày sinh không được để trống")
        LocalDate dob,

        @NotNull(message = "Năm nhập học không được để trống")
        Integer enrollment_year,

        @NotNull(message = "Mã khoa không được để trống")
        String facultyCode
) {
}
