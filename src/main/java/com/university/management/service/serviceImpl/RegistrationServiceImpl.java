package com.university.management.service.serviceImpl;

import com.university.management.model.dto.response.ClassResponse;
import com.university.management.model.entity.*;
import com.university.management.service.RegistrationService;

import com.university.management.repository.CourseClassRepository;
import com.university.management.repository.RegistrationRepository;
import com.university.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final CourseClassRepository courseClassRepository;
    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional
    public void registerCourse(String studentCode, String classCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên: " + studentCode));

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
        List<CourseClass> classes;

        if (facultyId != null) {
            classes = courseClassRepository.findClassesByFacultyOrShared(facultyId);
        } else {
            classes = courseClassRepository.findBySemester_IsActiveTrue();
        }

        return classes.stream().map(c -> {
            String facultyName = (c.getSubject().getFaculty() != null)
                    ? c.getSubject().getFaculty().getFacultyName()
                    : "Môn Đại Cương";

            return new ClassResponse(
                    c.getClassCode(),
                    c.getSubject().getSubjectName(),
                    facultyName,
                    c.getSubject().getCredits(),
                    c.getCurrentSlot(),
                    c.getMaxSlot()
            );
        }).toList();
    }
}
