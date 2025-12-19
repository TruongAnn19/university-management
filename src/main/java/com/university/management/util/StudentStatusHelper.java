package com.university.management.util;

import com.university.management.model.entity.Faculty;
import com.university.management.model.entity.Score;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.StudentStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class StudentStatusHelper {
    /**
     * Record để đóng gói kết quả tính toán bao gồm trạng thái và thông báo chi tiết
     */
    public record StudentStatusResult(StudentStatus status, String message) {}

    /**
     * Hàm trung tâm để xác định trạng thái sinh viên
     * @param student Đối tượng sinh viên cần xét
     * @param scores Danh sách điểm hiện có của sinh viên
     * @return StudentStatusResult chứa Status mới và Message giải thích
     */
    public StudentStatusResult determineStatus(Student student, List<Score> scores) {
        Faculty faculty = student.getFaculty();

        // 1. Kiểm tra dữ liệu đầu vào cơ bản
        if (faculty == null || student.getEnrollmentYear() == null) {
            return new StudentStatusResult(StudentStatus.STUDYING, "Thông tin Khoa hoặc Năm nhập học chưa hoàn thiện.");
        }

        // 2. Tính toán các thông số thời gian
        int currentYear = LocalDate.now().getYear();
        int yearsStudied = currentYear - student.getEnrollmentYear();
        int maxAllowedYears = faculty.getDuration() + 3; // Thời gian tối đa = Duration + 3 năm ân hạn

        // 3. Tính toán tổng tín chỉ tích lũy (Tổng các môn có điểm tổng kết >= 4.0)
        int accumulatedCredits = 0;
        if (scores != null) {
            accumulatedCredits = scores.stream()
                    .filter(s -> s.getTotalScore() != null && s.getTotalScore() >= 4.0)
                    .filter(s -> s.getSubject() != null)
                    .mapToInt(s -> s.getSubject().getCredits())
                    .sum();
        }

        // --- BẮT ĐẦU XÉT LOGIC ƯU TIÊN ---

        // ƯU TIÊN 1: Xét điều kiện Tốt nghiệp (Đủ tín chỉ)
        if (accumulatedCredits >= faculty.getRequiredCredits()) {
            return new StudentStatusResult(
                    StudentStatus.GRADUATED,
                    String.format("Xuất sắc! Đã tích lũy đủ %d/%d tín chỉ. Đủ điều kiện tốt nghiệp.",
                            accumulatedCredits, faculty.getRequiredCredits())
            );
        }

        // ƯU TIÊN 2: Xét điều kiện Buộc thôi học (Quá thời gian tối đa)
        if (yearsStudied > maxAllowedYears) {
            return new StudentStatusResult(
                    StudentStatus.EXPELLED,
                    String.format("Cảnh báo: Đã quá hạn đào tạo (%d/%d năm). Buộc thôi học.",
                            yearsStudied, maxAllowedYears)
            );
        }

        // ƯU TIÊN 3: Các trường hợp đang theo học
        int remainingCredits = faculty.getRequiredCredits() - accumulatedCredits;
        int yearsLeft = maxAllowedYears - yearsStudied;

        String progressMessage = String.format("Tiến độ: %d/%d tín chỉ. Còn thiếu %d tín chỉ. Thời gian còn lại: %d năm.",
                accumulatedCredits, faculty.getRequiredCredits(), remainingCredits, Math.max(0, yearsLeft));

        // Nếu đã hết thời gian chính thức nhưng chưa quá thời gian tối đa (đang trong 3 năm ân hạn)
        if (yearsStudied >= faculty.getDuration()) {
            return new StudentStatusResult(StudentStatus.DROPPED, "Cảnh báo: Đã quá thời gian đào tạo chuẩn. " + progressMessage);
        }

        // Mặc định là đang học bình thường
        return new StudentStatusResult(StudentStatus.STUDYING, progressMessage);
    }
}
