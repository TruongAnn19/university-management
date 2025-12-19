package com.university.management.util;

import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
public class StudentStatusHelper {
    private StudentStatus calculateCurrentStatus(Student student) {
        if (student.getEnrollmentYear() == null || student.getFaculty() == null) {
            return StudentStatus.STUDYING;
        }

        int currentYear = LocalDate.now().getYear();
        int enrollmentYear = student.getEnrollmentYear();
        int duration = student.getFaculty().getDuration();

        int maxYearAllowed = enrollmentYear + duration + 3;

        if (currentYear > maxYearAllowed) {
            return StudentStatus.EXPELLED;
        }

        if (student.getGpa() >= 2.0 && currentYear >= (enrollmentYear + duration)) return StudentStatus.GRADUATED;

        return StudentStatus.STUDYING;
    }
}
