package com.university.management.security;

import com.university.management.model.entity.Role;
import com.university.management.model.entity.Student;
import com.university.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("customSecurity")
@RequiredArgsConstructor
public class CustomSecurityService {
    private final StudentRepository studentRepository;

    public boolean isOwnerOrTeacher(String studentCode) {
        // 1. Lấy thông tin người đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // 2. Nếu là TEACHER hoặc ADMIN thì luôn cho phép (return true)
        boolean isTeacherOrAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.TEACHER.name())
                        || a.getAuthority().equals("ROLE_" + Role.ADMIN.name()));

        if (isTeacherOrAdmin) {
            return true;
        }

        // 3. Nếu là STUDENT, kiểm tra xem mã SV truyền vào có khớp với mã của tài khoản này không
        Student currentStudent = studentRepository.findByUser_Username(currentUsername)
                .orElse(null);

        if (currentStudent == null) {
            return false; // Tài khoản lỗi không gắn với SV nào -> Chặn
        }

        // So sánh mã SV của người dùng với mã SV họ đang muốn xem
        return currentStudent.getStudentCode().equals(studentCode);
    }
}
