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
import org.apache.poi.ss.usermodel.DataFormatter;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void importStudents(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String username = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String rawPassword = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String studentCode = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String fullName = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String className = dataFormatter.formatCellValue(row.getCell(4)).trim();
                String dobString = dataFormatter.formatCellValue(row.getCell(5)).trim();

                if (username.isEmpty() || studentCode.isEmpty()) continue;

                if (userRepository.findByUsername(username).isPresent()) {
                    continue;
                }

                var user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.STUDENT)
                        .build();
                userRepository.save(user);

                LocalDate dob;
                try {
                    dob = LocalDate.parse(dobString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    dob = LocalDate.of(2000, 1, 1);
                }

                var student = Student.builder()
                        .studentCode(studentCode)
                        .fullName(fullName)
                        .className(className)
                        .dob(dob)
                        .user(user)
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
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại: " + request.username());
        }

        if (studentRepository.findByStudentCode(request.studentCode()).isPresent()) {
            throw new RuntimeException("Mã sinh viên đã tồn tại: " + request.studentCode());
        }

        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.STUDENT)
                .build();
        userRepository.save(user);

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
