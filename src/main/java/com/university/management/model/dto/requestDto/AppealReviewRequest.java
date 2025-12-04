package com.university.management.model.dto.requestDto;

import com.university.management.model.entity.AppealStatus;
import jakarta.validation.constraints.NotNull;

public record AppealReviewRequest(
        @NotNull(message = "Trạng thái duyệt không được trống")
        AppealStatus status,

        String teacherResponse,
        Double newScore
) {
}
