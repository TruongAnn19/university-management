package com.university.management.scheduler;

import com.university.management.model.entity.Score;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.util.StudentStatusHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentStatusScheduler {
    private final StudentRepository studentRepository;
    private final ScoreRepository scoreRepository;
    private final StudentStatusHelper statusHelper;

    @Scheduled(cron = "0 0 0 1 9 ?")
    @Transactional
    public void updateExpelledStudentsOptimized() {
        int currentYear = LocalDate.now().getYear();
        int pageSize = 500;
        int pageNum = 0;
        Page<Student> studentPage;
        do {
            studentPage = studentRepository.findByStatusIn(
                    List.of(StudentStatus.STUDYING, StudentStatus.DROPPED),
                    PageRequest.of(pageNum, pageSize)
            );
            List<Student> toUpdate = new ArrayList<>();
            for (Student student : studentPage.getContent()) {
                List<Score> scores = scoreRepository.findByStudent_StudentCode(student.getStudentCode());
                StudentStatusHelper.StudentStatusResult result = statusHelper.determineStatus(student, scores, currentYear);

                if (student.getStatus() != result.status()) {
                    student.setStatus(result.status());
                    toUpdate.add(student);
                }
            }
            studentRepository.saveAll(toUpdate);
            studentRepository.flush();
            pageNum++;
        } while (studentPage.hasNext());
    }
}
