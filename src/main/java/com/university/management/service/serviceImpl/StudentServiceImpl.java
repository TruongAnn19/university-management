package com.university.management.service.serviceImpl;

import com.university.management.model.dto.requestDto.StudentRequestDto;
import com.university.management.model.entity.Role;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.User;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.UserRepository;
import com.university.management.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Cần cái này để mã hóa pass "123" -> "$2a$..."

    @Override
    @Transactional // Quan trọng: Nếu lỗi ở bước tạo Student thì Rollback luôn cả User
    public void importStudents(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Duyệt từ dòng 1 (bỏ dòng tiêu đề)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 1. Đọc dữ liệu từ Excel
                String username = row.getCell(0).getStringCellValue();
                String rawPassword = "";

                // Xử lý password (đôi khi excel hiểu số 123 là number)
                if (row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                    rawPassword = String.valueOf((int) row.getCell(1).getNumericCellValue());
                } else {
                    rawPassword = row.getCell(1).getStringCellValue();
                }

                String studentCode = row.getCell(2).getStringCellValue();
                String fullName = row.getCell(3).getStringCellValue();
                String className = row.getCell(4).getStringCellValue();
                String dobString = row.getCell(5).getStringCellValue(); // Giả sử nhập text: 2006-01-15

                // Kiểm tra trùng lặp
                if (userRepository.findByUsername(username).isPresent()) {
                    continue; // Nếu user tồn tại rồi thì bỏ qua (hoặc ném lỗi tùy bạn)
                }

                // 2. TẠO USER TRƯỚC
                var user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword)) // Mã hóa ngay
                        .role(Role.STUDENT)
                        .build();

                // Lưu User để có ID (nhưng chưa commit transaction)
                // Trong JPA, khi set User vào Student với CascadeType.ALL, ta có thể không cần save user riêng
                // Nhưng save riêng cho chắc chắn và dễ debug
                user = userRepository.save(user);

                // 3. TẠO STUDENT VÀ GẮN USER VÀO
                var student = Student.builder()
                        .studentCode(studentCode)
                        .fullName(fullName)
                        .className(className)
                        .dob(LocalDate.parse(dobString, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .user(user) // <--- LIÊN KẾT TẠI ĐÂY
                        .build();

                studentRepository.save(student);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import sinh viên: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void createStudent(StudentRequestDto request) {
        // 1. Kiểm tra trùng Username
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại: " + request.username());
        }

        // 2. Kiểm tra trùng Mã SV
        if (studentRepository.findByStudentCode(request.studentCode()).isPresent()) {
            throw new RuntimeException("Mã sinh viên đã tồn tại: " + request.studentCode());
        }

        // 3. Tạo User (Tài khoản)
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

        // 4. Tạo Student (Hồ sơ)
        var student = Student.builder()
                .studentCode(request.studentCode())
                .fullName(request.fullName())
                .className(request.className())
                .dob(request.dob())
                .user(user)
                .build();

        studentRepository.save(student);
    }
}
