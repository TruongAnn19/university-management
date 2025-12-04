package com.university.management.repository;

import com.university.management.model.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Optional<Semester> findByIsActiveTrue();
    Optional<Semester> findBySemesterCode(String code);
}
