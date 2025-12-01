package com.university.management.service.serviceImpl;

import com.university.management.model.dto.requestDto.TeacherRequestDto;
import com.university.management.model.entity.*;
import com.university.management.repository.FacultyRepository;
import com.university.management.repository.TeacherRepository;
import com.university.management.repository.UserRepository;
import com.university.management.service.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void importTeachers(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Duyệt từ dòng 1 (bỏ dòng tiêu đề)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                int currentRowIndex = i;

                String username = row.getCell(0).getStringCellValue();

                String rawPassword = "";
                if (row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
                    rawPassword = String.valueOf((int) row.getCell(1).getNumericCellValue());
                } else {
                    rawPassword = row.getCell(1).getStringCellValue();
                }

                String teacherCode = row.getCell(2).getStringCellValue();
                String fullName = row.getCell(3).getStringCellValue();
                String degree = row.getCell(4).getStringCellValue();

                String facultyCode = row.getCell(5).getStringCellValue();

                // Kiểm tra User trùng lặp (nếu cần)
                if (userRepository.findByUsername(username).isPresent()) continue;

                // 4. TÌM KHOA TRONG DB (Validation)
                // Lúc này biến facultyCode đã có giá trị lấy từ Excel
                Faculty faculty = facultyRepository.findByFacultyCode(facultyCode)
                        .orElseThrow(() -> new RuntimeException("Lỗi dòng " + (currentRowIndex+1) + ": Mã khoa không tồn tại (" + facultyCode + ")"));

                // 5. TẠO USER
                var user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.TEACHER)
                        .build();
                userRepository.save(user);

                // 6. TẠO TEACHER GẮN VỚI KHOA
                var teacher = Teacher.builder()
                        .teacherCode(teacherCode)
                        .fullName(fullName)
                        .degree(degree)       // Set trình độ
                        .user(user)           // Gắn tài khoản
                        .faculty(faculty)     // Gắn khoa vừa tìm được
                        .build();

                teacherRepository.save(teacher);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import giảng viên: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void createTeacher(TeacherRequestDto request) {
        // 1. Check trùng Username
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại: " + request.username());
        }

        // 2. Check trùng Mã GV
        if (teacherRepository.findByTeacherCode(request.teacherCode()).isPresent()) {
            throw new RuntimeException("Mã giảng viên đã tồn tại: " + request.teacherCode());
        }

        // 3. Tìm Khoa
        Faculty faculty = facultyRepository.findByFacultyCode(request.facultyCode())
                .orElseThrow(() -> new RuntimeException("Mã khoa không tồn tại: " + request.facultyCode()));

        // 4. Tạo User
        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.TEACHER)
                .build();
        userRepository.save(user);

        // 5. Tạo Teacher
        var teacher = Teacher.builder()
                .teacherCode(request.teacherCode())
                .fullName(request.fullName())
                .degree(request.degree())
                .user(user)
                .faculty(faculty)
                .build();

        teacherRepository.save(teacher);
    }
}
