package com.university.management.repository;

import com.university.management.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findBySubjectCodeIn(java.util.Collection<String> subjectCodes);
}
