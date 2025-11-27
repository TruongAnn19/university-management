package com.university.management.repository;

import com.university.management.model.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findByStudent_StudentCode(String studentCode);
}
