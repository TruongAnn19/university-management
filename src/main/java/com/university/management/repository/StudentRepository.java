package com.university.management.repository;

import com.university.management.model.dto.StatPair;
import com.university.management.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByUser_Username(String username);
    @Query("SELECT new com.university.management.model.dto.StatPair(f.facultyName, COUNT(s)) " +
            "FROM Student s JOIN s.faculty f " +
            "GROUP BY f.facultyName")
    List<StatPair> countStudentsByFaculty();
}
