package com.university.management.repository;

import com.university.management.model.dto.StatPair;
import com.university.management.model.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score,Long> {
    List<Score> findByStudent_StudentCode(String studentCode);
    Optional<Score> findByStudent_StudentCodeAndSubject_SubjectCode(String studentCode, String subjectCode);
    @Query("SELECT new com.university.management.model.dto.StatPair(sub.subjectName, AVG(s.finalScore)) " +
            "FROM Score s JOIN s.subject sub " +
            "GROUP BY sub.subjectName")
    List<StatPair> getAvgScoreBySubject();
}
