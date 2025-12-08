package com.university.management.repository;

import com.university.management.model.entity.CourseClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseClassRepository extends CrudRepository<CourseClass, Long> {
    Optional<CourseClass> findByClassCode(String classCode);

    @Query("SELECT c FROM CourseClass c " +
            "WHERE c.semester.isActive = true " +
            "AND (c.subject.faculty.id = :facultyId OR c.subject.faculty IS NULL)")
    List<CourseClass> findClassesByFacultyOrShared(@Param("facultyId") Long facultyId);

    List<CourseClass> findBySemester_IsActiveTrue();


}
