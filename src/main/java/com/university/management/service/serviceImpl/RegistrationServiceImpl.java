package com.university.management.service.serviceImpl;

import com.university.management.model.entity.CourseClass;
import com.university.management.model.entity.Registration;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import com.university.management.service.RegistrationService;

import com.university.management.repository.CourseClassRepository;
import com.university.management.repository.RegistrationRepository;
import com.university.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final CourseClassRepository courseClassRepository;
    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;

    @Override
    @Transactional // Quản lý giao dịch: Nếu lỗi sẽ rollback toàn bộ
    public void registerCourse(String studentCode, String classCode) {
        // 1. Tìm Sinh viên
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên: " + studentCode));

        if (student.getStatus() == StudentStatus.GRADUATED) {
            throw new RuntimeException("Bạn đã tốt nghiệp! Không thể đăng ký thêm môn học.");
        }

        if (student.getStatus() == StudentStatus.EXPELLED) {
            throw new RuntimeException("Tài khoản đang bị khóa do buộc thôi học. Vui lòng liên hệ phòng đào tạo.");
        }
        // 2. Tìm Lớp học phần
        CourseClass courseClass = courseClassRepository.findByClassCode(classCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần: " + classCode));

        // 3. Kiểm tra xem đã đăng ký chưa (Tránh đăng ký trùng)
        if (registrationRepository.existsByStudentAndCourseClass(student, courseClass)) {
            throw new RuntimeException("Bạn đã đăng ký lớp này rồi!");
        }

        // 4. Kiểm tra chỗ trống (Logic nghiệp vụ)
        if (courseClass.getCurrentSlot() >= courseClass.getMaxSlot()) {
            throw new RuntimeException("Lớp đã đầy! Không thể đăng ký thêm.");
        }

        // 5. Tăng số lượng & Lưu Class
        // (Cơ chế @Version trong Entity sẽ tự động chặn nếu có xung đột dữ liệu)
        courseClass.setCurrentSlot(courseClass.getCurrentSlot() + 1);
        courseClassRepository.save(courseClass);

        // 6. Lưu biên lai đăng ký (Registration)
        Registration registration = Registration.builder()
                .student(student)
                .courseClass(courseClass)
                .registerTime(LocalDateTime.now())
                .build();

        registrationRepository.save(registration);
    }
}
