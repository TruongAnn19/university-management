package com.university.management.service.serviceImpl;

import com.university.management.mapper.ScoreMapper;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.dto.response.TranscriptResponse;
import com.university.management.model.entity.*;
import com.university.management.repository.*;
import com.university.management.service.AuditService;
import com.university.management.service.ScoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreMapper scoreMapper;
    private final AuditService auditService;
    private final SemesterRepository semesterRepository;
    private final RegistrationRepository registrationRepository;

    @Override
//    @CacheEvict(value = "student_scores", key = "#request.studentCode()")
    public ScoreDto recordScore(ScoreRequestDto request) {
        String currentUsername = "SYSTEM";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sinh viên mã " + request.getStudentCode()));

        Subject subject = subjectRepository.findBySubjectCode(request.getSubjectCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy môn học mã " + request.getSubjectCode()));

        Semester currentSemester = semesterRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Lỗi: Không có học kỳ nào đang mở!"));

        Optional<Score> existingScoreOpt = scoreRepository.findByStudent_StudentCodeAndSubject_SubjectCodeAndSemester(
                request.getStudentCode(), request.getSubjectCode(), currentSemester);

        Score scoreToSave;
        String actionType;
        String oldValueStr = "N/A";

        if (existingScoreOpt.isPresent()) {
            actionType = "UPDATE_SCORE";
            Score existingScore = existingScoreOpt.get();
            oldValueStr = String.format("Process: %.1f, Final: %.1f",
                    existingScore.getProcessScore(), existingScore.getFinalScore());

            scoreToSave = existingScoreOpt.get();
            scoreToSave.setProcessScore(request.getProcessScore());
            scoreToSave.setFinalScore(request.getFinalScore());
        } else {
            actionType = "CREATE_SCORE";
            scoreToSave = Score.builder()
                    .student(student)
                    .subject(subject)
                    .semester(currentSemester)
                    .processScore(request.getProcessScore())
                    .finalScore(request.getFinalScore())
                    .build();
        }

        Score savedScore = scoreRepository.save(scoreToSave);
        String newValueStr = String.format("Process: %.1f, Final: %.1f",
                savedScore.getProcessScore(), savedScore.getFinalScore());

        auditService.logAction(
                actionType,
                "SCORE",
                savedScore.getId().toString(),
                oldValueStr,
                newValueStr,
                currentUsername
        );

        return scoreMapper.toDto(savedScore);
    }

    @Override
    @Transactional
//    @Cacheable(value = "student_scores", key = "#studentCode")
    public TranscriptResponse getStudentTranscript(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Faculty faculty = student.getFaculty();
        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);
        List<ScoreDto> scoresDto = scores.stream().map(scoreMapper::toDto).toList();

        if (faculty == null) {
            return new TranscriptResponse(student.getFullName(), "N/A",
                    student.getStatus().name(), "Chưa cập nhật thông tin Khoa", scoresDto);
        }

        if (student.getStatus() == StudentStatus.GRADUATED) {
            return new TranscriptResponse(student.getFullName(), faculty.getFacultyName(),
                    "ĐÃ TỐT NGHIỆP", "Chúc mừng! Bạn đã hoàn thành chương trình đào tạo.", scoresDto);
        }
        if (student.getStatus() == StudentStatus.EXPELLED) {
            return new TranscriptResponse(student.getFullName(), faculty.getFacultyName(),
                    "BUỘC THÔI HỌC", "Cảnh báo: Bạn đã bị buộc thôi học do quá thời gian đào tạo.", scoresDto);
        }

        int currentYear = Year.now().getValue();
        int yearsStudied = currentYear - student.getEnrollmentYear();
        int maxAllowedYears = faculty.getDuration() + 3;
        int accumulatedCredits = scores.stream()
                .filter(s -> s.getTotalScore() != null && s.getTotalScore() >= 4.0)
                .mapToInt(s -> s.getSubject().getCredits())
                .sum();

        String statusStr = "ĐANG THEO HỌC";
        String message;

        if (accumulatedCredits >= faculty.getRequiredCredits()) {
            student.setStatus(StudentStatus.GRADUATED);
            statusStr = "ĐÃ TỐT NGHIỆP";
            message = String.format("Xuất sắc! Bạn đã tích lũy đủ %d/%d tín chỉ.",
                    accumulatedCredits, faculty.getRequiredCredits());
        } else if (yearsStudied > maxAllowedYears) {
            student.setStatus(StudentStatus.EXPELLED);
            statusStr = "BUỘC THÔI HỌC";
            message = String.format("Bạn đã học %d năm (Quá hạn tối đa %d năm). Kết quả học tập bị hủy bỏ.",
                    yearsStudied, maxAllowedYears);
        } else {
            int remainingCredits = faculty.getRequiredCredits() - accumulatedCredits;
            int remainingYears = maxAllowedYears - yearsStudied;
            message = String.format("Tiến độ: %d/%d tín chỉ. Còn thiếu %d tín chỉ. Bạn còn %d năm để hoàn thành.",
                    accumulatedCredits, faculty.getRequiredCredits(), remainingCredits, remainingYears);
        }

        studentRepository.save(student);

        return new TranscriptResponse(
                student.getFullName(),
                faculty.getFacultyName(),
                statusStr,
                message,
                scoresDto
        );
    }

    @Override
    @Transactional
    public TranscriptResponse getMyTranscriptByUsername(String username) {
        Student student = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Tài khoản này chưa liên kết với hồ sơ sinh viên nào!"));
        String realStudentCode = student.getStudentCode();
        return this.getStudentTranscript(realStudentCode);
    }

    @Override
    @Transactional
//    @CacheEvict(value = "student_scores", allEntries = true)
    public void importScores(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File excel rỗng");
        }

        Semester currentSemester = semesterRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Không có học kỳ nào đang mở đăng ký!"));

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectCode = dataFormatter.formatCellValue(row.getCell(1)).trim();

                if (studentCode.isEmpty() || subjectCode.isEmpty()) continue;

                boolean exists = scoreRepository.findByStudent_StudentCodeAndSubject_SubjectCodeAndSemester(
                        studentCode, subjectCode, currentSemester
                ).isPresent();

                if (exists) {
                    continue;
                }

                List<Registration> registrations = registrationRepository
                        .findByStudent_StudentCodeAndCourseClass_Subject_SubjectCode(studentCode, subjectCode);

                if (registrations.isEmpty()) {
                    throw new RuntimeException(studentCode + "CHƯA TỪNG ĐĂNG KÝ môn " + subjectCode + ". Không thể nhập điểm!");
                }

                boolean isStudying = registrations.stream()
                        .anyMatch(r -> Boolean.TRUE.equals(r.getCourseClass().getSemester().getIsActive()));

                if (isStudying) {
                    throw new RuntimeException("Sinh viên đang trong thời gian học môn " + subjectCode + ". Vui lòng đợi kết thúc học kỳ để nhập điểm.");
                }

                double processScore = 0.0;
                double finalScore = 0.0;
                try {
                    String pScoreStr = dataFormatter.formatCellValue(row.getCell(2)).trim();
                    String fScoreStr = dataFormatter.formatCellValue(row.getCell(3)).trim();

                    processScore = pScoreStr.isEmpty() ? 0.0 : Double.parseDouble(pScoreStr);
                    finalScore = fScoreStr.isEmpty() ? 0.0 : Double.parseDouble(fScoreStr);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Lỗi định dạng điểm số tại dòng " + (i + 1));
                }

                var requestDto = new ScoreRequestDto(
                        studentCode,
                        subjectCode,
                        processScore,
                        finalScore
                );

                this.recordScore(requestDto);
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi dữ liệu tại dòng đang xử lý: " + e.getMessage());
        }
    }

}
