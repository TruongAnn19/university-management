package com.university.management.service.serviceImpl;

import com.university.management.mapper.StudentMapper;
import com.university.management.model.dto.StudentDto;
import com.university.management.model.dto.requestDto.TeacherRequestDto;
import com.university.management.model.entity.*;
import com.university.management.repository.FacultyRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import com.university.management.repository.UserRepository;
import com.university.management.service.TeacherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    @Transactional
    public void importTeachers(MultipartFile file) {
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
                String teacherCode = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String fullName = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String degree = dataFormatter.formatCellValue(row.getCell(4)).trim();
                String facultyCode = dataFormatter.formatCellValue(row.getCell(5)).trim();

                if (username.isEmpty() || teacherCode.isEmpty()) continue;

                if (userRepository.findByUsername(username).isPresent()) continue;
                if (teacherRepository.findByTeacherCode(teacherCode).isPresent()) continue;

                Faculty faculty = facultyRepository.findByFacultyCode(facultyCode)
                        .orElseThrow(() -> new RuntimeException(
                                "Lỗi dòng " + (currentRowIndex+1) + ": Mã khoa '" + facultyCode + "' không tồn tại trong hệ thống."
                        ));

                var user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .role(Role.TEACHER)
                        .build();
                userRepository.save(user);

                var teacher = Teacher.builder()
                        .teacherCode(teacherCode)
                        .fullName(fullName)
                        .degree(degree)
                        .user(user)
                        .faculty(faculty)
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
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username đã tồn tại: " + request.username());
        }

        if (teacherRepository.findByTeacherCode(request.teacherCode()).isPresent()) {
            throw new RuntimeException("Mã giảng viên đã tồn tại: " + request.teacherCode());
        }

        Faculty faculty = facultyRepository.findByFacultyCode(request.facultyCode())
                .orElseThrow(() -> new RuntimeException("Mã khoa không tồn tại: " + request.facultyCode()));

        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.TEACHER)
                .build();
        userRepository.save(user);

        var teacher = Teacher.builder()
                .teacherCode(request.teacherCode())
                .fullName(request.fullName())
                .degree(request.degree())
                .user(user)
                .faculty(faculty)
                .build();

        teacherRepository.save(teacher);
    }
    @Override
    public List<StudentDto> getListStudent(String facultyCode) {
        List<Student> students = studentRepository.findByFaculty_FacultyCode(facultyCode);
        return studentMapper.toDtoList(students);
    }
}
