package com.university.management.scheduler;

import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import com.university.management.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentStatusScheduler {
    private final StudentRepository studentRepository;

    @Scheduled(cron = "0 0 0 1 9 ?")
    @Transactional
    public void updateExpelledStudents() {
        List<Student> activeStudents = studentRepository.findAllByStatus(StudentStatus.STUDYING);
        int currentYear = LocalDate.now().getYear();

        for (Student student : activeStudents) {
            int maxYearAllowed = student.getEnrollmentYear() + student.getFaculty().getDuration() + 3;
            if (currentYear > maxYearAllowed) {
                student.setStatus(StudentStatus.EXPELLED);
            }
        }
        studentRepository.saveAll(activeStudents);
    }
}
