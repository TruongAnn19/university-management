package com.university.management.model.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppealRequest(
        @NotNull(message = "ID điểm số không được để trống")
        Long scoreId,

        @NotBlank(message = "Lý do phúc khảo không được để trống")
        String reason
) { }
