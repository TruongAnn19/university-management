package com.university.management.model.dto.requestDto;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequestDto(
//        @NotBlank(message = "Mã sinh viên không được để trống")
//        String studentCode,

        @NotBlank(message = "Mã lớp học phần không được để trống")
        String classCode
) { }
