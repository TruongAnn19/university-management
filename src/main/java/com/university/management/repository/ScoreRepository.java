package com.university.management.repository;

import com.university.management.model.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findByStudent_StudentCode(String studentCode);
    Optional<Score> findByStudent_StudentCodeAndSubject_SubjectCode(String studentCode, String subjectCode);
}
