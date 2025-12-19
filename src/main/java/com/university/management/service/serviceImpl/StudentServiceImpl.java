package com.university.management.service.serviceImpl;

import com.university.management.model.dto.requestDto.StudentRequestDto;
import com.university.management.model.entity.*;
import com.university.management.repository.FacultyRepository;
import com.university.management.repository.ScoreRepository;
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
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FacultyRepository facultyRepository;
    private final ScoreRepository scoreRepository;

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
                int currentRowIndex = i;

                String username = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String rawPassword = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String studentCode = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String fullName = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String className = dataFormatter.formatCellValue(row.getCell(4)).trim();
                String dobString = dataFormatter.formatCellValue(row.getCell(5)).trim();
                String facultyCode = dataFormatter.formatCellValue(row.getCell(6)).trim();
                Integer enrollmentYear =  Integer.parseInt(dataFormatter.formatCellValue(row.getCell(7)).trim());

                if (username.isEmpty() || studentCode.isEmpty()) continue;
                if (userRepository.findByUsername(username).isPresent()) {
                    continue;
                }

                if (facultyCode.isEmpty()) {
                    throw new RuntimeException("Dòng " + (i+1) + ": Thiếu mã khoa!");
                }
                Faculty faculty = facultyRepository.findByFacultyCode(facultyCode)
                        .orElseThrow(() -> new RuntimeException("Dòng " + (currentRowIndex+1) + ": Mã khoa '" + facultyCode + "' không tồn tại."));

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
                        .faculty(faculty)
                        .user(user)
                        .status(StudentStatus.STUDYING)
                        .enrollmentYear(enrollmentYear)
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

        Faculty faculty = facultyRepository.findByFacultyCode(request.facultyCode())
                .orElseThrow(() -> new RuntimeException("Khoa không tồn tại"));

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
                .enrollmentYear(request.enrollment_year())
                .faculty(faculty)
                .status(StudentStatus.STUDYING)
                .user(user)
                .build();

        studentRepository.save(student);
    }

    private double convertToGpa4(double score10) {
        if (score10 >= 8.5) return 4.0;
        if (score10 >= 7.0) return 3.0;
        if (score10 >= 5.5) return 2.0;
        if (score10 >= 4.0) return 1.0;
        return 0.0;
    }

    @Transactional
    public void updateStudentGpa(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);

        if (scores.isEmpty()) {
            student.setGpa(0.0);
            studentRepository.save(student);
            return;
        }

        double totalScore4 = 0;
        int totalCredits = 0;

        for (Score s : scores) {
            if (s.getTotalScore() != null) {
                int credits = s.getSubject().getCredits();
                double gpa4 = convertToGpa4(s.getTotalScore());

                totalScore4 += gpa4 * credits;
                totalCredits += credits;
            }
        }

        if (totalCredits > 0) {
            double finalGpa = totalScore4 / totalCredits;
            finalGpa = Math.round(finalGpa * 100.0) / 100.0;

            student.setGpa(finalGpa);
        } else {
            student.setGpa(0.0);
        }

        studentRepository.save(student);
    }

    private StudentStatus determineStatus(Student student) {
        if (student.getGpa() == null || student.getGpa() == 0) {
            return StudentStatus.STUDYING;
        }

        // 2. Kiểm tra dựa trên GPA
        double gpa = student.getGpa();
        if (gpa < 1.0) return StudentStatus.EXPELLED;
        if (gpa < 2.0) return StudentStatus.DROPPED;

        int currentYear = LocalDate.now().getYear();
        if (student.getEnrollmentYear() != null && currentYear > student.getEnrollmentYear()) {
            return gpa >= 2.0 ? StudentStatus.GRADUATED : StudentStatus.STUDYING;
        }

        return StudentStatus.STUDYING;
    }
}
