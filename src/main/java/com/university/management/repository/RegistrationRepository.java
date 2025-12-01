package com.university.management.repository;

import com.university.management.model.entity.CourseClass;
import com.university.management.model.entity.Registration;
import com.university.management.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByStudentAndCourseClass(Student student, CourseClass courseClass);
}
