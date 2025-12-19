package com.university.management.repository;

import com.university.management.model.dto.StatPair;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByStudentCode(String studentCode);
    Optional<Student> findByUser_Username(String username);
    @Query("SELECT new com.university.management.model.dto.StatPair(f.facultyName, COUNT(s)) " +
            "FROM Student s JOIN s.faculty f " +
            "GROUP BY f.facultyName")
    List<StatPair> countStudentsByFaculty();

    Optional<Student> findByUserId(Long id);
    List<Student> findByFaculty_FacultyCode(String facultyCode);

    List<Student> findAllByStatus(StudentStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Student s SET s.status = 'EXPELLED' " +
            "WHERE s.status = 'STUDYING' " +
            "AND (:currentYear > (s.enrollmentYear + s.faculty.duration + 3))")
    void updateExpelledStatus(@Param("currentYear") int currentYear);
}
