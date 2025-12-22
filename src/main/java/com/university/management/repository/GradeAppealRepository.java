package com.university.management.repository;

import com.university.management.model.entity.AppealStatus;
import com.university.management.model.entity.GradeAppeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeAppealRepository extends JpaRepository<GradeAppeal, Long> {
    List<GradeAppeal> findByStudent_StudentCode(String studentCode);
    List<GradeAppeal> findByStatus(AppealStatus status);
    boolean existsByStudent_StudentCodeAndScore_Id(String studentCode, Long scoreId);
}
