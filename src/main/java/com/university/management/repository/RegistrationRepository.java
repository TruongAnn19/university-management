package com.university.management.repository;

import com.university.management.model.entity.CourseClass;
import com.university.management.model.entity.Registration;
import com.university.management.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    boolean existsByStudentAndCourseClass(Student student, CourseClass courseClass);

    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
            "WHERE r.student.id = :studentId " +
            "AND r.courseClass.semester.id = :semesterId " +
            "AND r.courseClass.subject.id = :subjectId")
    boolean existsByStudentAndSemesterAndSubject(@Param("studentId") Long studentId,
                                                 @Param("semesterId") Long semesterId,
                                                 @Param("subjectId") Long subjectId);
    List<Registration> findByStudent_IdAndCourseClass_Semester_IsActiveTrue(Long student);
    Optional<Registration> findByStudent_User_UsernameAndCourseClass_ClassCode(String username, String classCode);
    List<Registration> findByStudent_StudentCodeAndCourseClass_Subject_SubjectCode(
            String studentCode,
            String subjectCode
    );
}
