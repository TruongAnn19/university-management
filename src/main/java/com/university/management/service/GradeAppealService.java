package com.university.management.service;

import com.university.management.model.dto.requestDto.AppealRequest;
import com.university.management.model.dto.requestDto.AppealReviewRequest;
import com.university.management.model.entity.GradeAppeal;

import java.util.List;

public interface GradeAppealService {
    void createAppeal(String studentCode, AppealRequest request);
    void reviewAppeal(Long appealId, AppealReviewRequest request);
    List<GradeAppeal> getMyAppeals(String studentCode);
    List<GradeAppeal> getPendingAppeals();
}
