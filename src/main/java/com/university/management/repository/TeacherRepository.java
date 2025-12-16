package com.university.management.repository;

import com.university.management.model.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByTeacherCode(String teacherCode);

    List<Teacher> findByFaculty_FacultyCode(String facultyCode);

    Optional<Teacher> findByUser_Username(String username);

    @Query("""
            SELECT t FROM Teacher t
            LEFT JOIN FETCH t.faculty
            WHERE t.user.id = :userId
            """)
    Optional<Teacher> findByUserId(@Param("userId") Long id);
}
