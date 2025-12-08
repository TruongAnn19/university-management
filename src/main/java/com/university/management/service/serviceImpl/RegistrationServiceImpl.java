package com.university.management.service.serviceImpl;

import com.university.management.model.dto.response.ClassResponse;
import com.university.management.model.entity.*;
import com.university.management.repository.CourseClassRepository;
import com.university.management.repository.RegistrationRepository;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final CourseClassRepository courseClassRepository;
    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;
    private final ScoreRepository scoreRepository;

    @Override
    @Transactional
    public void registerCourse(String username, String classCode) {
        Student student = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản này chưa liên kết với hồ sơ sinh viên nào!"));

        if (student.getStatus() == StudentStatus.GRADUATED) {
            throw new RuntimeException("Bạn đã tốt nghiệp! Không thể đăng ký thêm môn học.");
        }

        if (student.getStatus() == StudentStatus.EXPELLED) {
            throw new RuntimeException("Tài khoản đang bị khóa do buộc thôi học. Vui lòng liên hệ phòng đào tạo.");
        }

        CourseClass courseClass = courseClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần: " + classCode));

        Semester classSemester = courseClass.getSemester();

        if (classSemester == null || !Boolean.TRUE.equals(classSemester.getIsActive())) {
            throw new RuntimeException("Lớp học phần này thuộc học kỳ đã đóng hoặc chưa mở đăng ký!");
        }

        boolean isDuplicateSubject = registrationRepository.existsByStudentAndSemesterAndSubject(
                student.getId(),
                classSemester.getId(),
                courseClass.getSubject().getId()
        );

        if (isDuplicateSubject) {
            throw new RuntimeException("Bạn đã đăng ký môn học này (" +
                    courseClass.getSubject().getSubjectName() + ") trong kỳ này rồi!");
        }

        if (courseClass.getCurrentSlot() >= courseClass.getMaxSlot()) {
            throw new RuntimeException("Lớp đã đầy! Không thể đăng ký thêm.");
        }

        courseClass.setCurrentSlot(courseClass.getCurrentSlot() + 1);
        courseClassRepository.save(courseClass);

        Registration registration = Registration.builder()
                .student(student)
                .courseClass(courseClass)
                .registerTime(LocalDateTime.now())
                .build();

        registrationRepository.save(registration);
    }

    @Override
    public List<ClassResponse> getOpenClasses(Long facultyId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByUser_Username(currentUsername).orElse(null);

        Map<Long, Double> studentScoreMap = new HashMap<>();

        if (student != null) {
            List<Score> scores = scoreRepository.findByStudent_StudentCode(student.getStudentCode());
            for (Score s : scores) {
                Long subjectId = s.getSubject().getId();
                Double currentVal = s.getTotalScore() == null ? 0.0 : s.getTotalScore();

                if (!studentScoreMap.containsKey(subjectId) || currentVal > studentScoreMap.get(subjectId)) {
                    studentScoreMap.put(subjectId, currentVal);
                }
            }
        }

        Set<Long> registeredSubjectIds = new HashSet<>();
        Set<String> registeredClassCodes = new HashSet<>();
        if (student != null) {
            List<Registration> regs = registrationRepository.findByStudent_IdAndCourseClass_Semester_IsActiveTrue(student.getId());
            for (Registration r : regs) {
                registeredSubjectIds.add(r.getCourseClass().getSubject().getId());
                registeredClassCodes.add(r.getCourseClass().getClassCode());
            }
        }

        List<CourseClass> classes;
        if (facultyId != null) {
            classes = courseClassRepository.findClassesByFacultyOrShared(facultyId);
        } else {
            classes = courseClassRepository.findBySemester_IsActiveTrue();
        }
        LocalDate today = LocalDate.now();
        return classes.stream().map(c -> {
            String facultyName = (c.getSubject().getFaculty() != null)
                    ? c.getSubject().getFaculty().getFacultyName()
                    : "Môn Đại Cương";
            String status = "NEW";
            Long subjectId = c.getSubject().getId();
            boolean isRegistered = registeredSubjectIds.contains(c.getSubject().getId());
            boolean isMyClass = registeredClassCodes.contains(c.getClassCode());
            if (studentScoreMap.containsKey(subjectId)) {
                Double maxScore = studentScoreMap.get(subjectId);
                if (maxScore >= 4.0) {
                    status = "PASSED";
                } else {
                    status = "RETAKE";
                }
            }
            Semester sem = c.getSemester();
            LocalDate start = sem.getStartDate();
            LocalDate openRegDate = start.minusWeeks(2);
            String timeStatus = "OPEN";
            if (today.isBefore(openRegDate)) {
                timeStatus = "WAITING";
            } else if (today.isAfter(start) || today.isEqual(start)) {
                timeStatus = "EXPIRED";
            }
            return new ClassResponse(
                    c.getClassCode(),
                    c.getSubject().getSubjectName(),
                    facultyName,
                    c.getSubject().getCredits(),
                    c.getCurrentSlot(),
                    c.getMaxSlot(),
                    status,
                    isRegistered,
                    isMyClass,
                    sem.getSemesterName(),
                    sem.getStartDate(),
                    sem.getEndDate(),
                    timeStatus
            );
        }).toList();
    }

    @Override
    @Transactional
    public void cancelRegistration(String username, String classCode) {
        Registration registration = registrationRepository.findByStudent_User_UsernameAndCourseClass_ClassCode(username, classCode)
                .orElseThrow(() -> new RuntimeException("Bạn chưa đăng ký lớp học phần này!"));

        if (!Boolean.TRUE.equals(registration.getCourseClass().getSemester().getIsActive())) {
            throw new RuntimeException("Không thể hủy lớp thuộc học kỳ đã đóng.");
        }

        CourseClass courseClass = registration.getCourseClass();
        if (courseClass.getCurrentSlot() > 0) {
            courseClass.setCurrentSlot(courseClass.getCurrentSlot() - 1);
            courseClassRepository.save(courseClass);
        }

        // 4. Xóa bản ghi đăng ký
        registrationRepository.delete(registration);
    }
}
