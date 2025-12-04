package com.university.management.repository;

import com.university.management.model.entity.CourseClass;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseClassRepository extends CrudRepository<CourseClass, Long> {
    Optional<CourseClass> findByClassCode(String classCode);
}
