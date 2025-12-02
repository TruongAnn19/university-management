package com.university.management.service.serviceImpl;

import com.university.management.mapper.ScoreMapper;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.dto.response.TranscriptResponse;
import com.university.management.model.entity.*;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.SubjectRepository;
import com.university.management.service.AuditService;
import com.university.management.service.ScoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Override
    @CacheEvict(value = "student_scores", key = "#request.studentCode()")
    public ScoreDto recordScore(ScoreRequestDto request) {
        String currentUsername = "SYSTEM";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sinh viên mã " + request.getStudentCode()));

        Subject subject = subjectRepository.findBySubjectCode(request.getSubjectCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy môn học mã " + request.getSubjectCode()));

        Optional<Score> existingScoreOpt = scoreRepository.findByStudent_StudentCodeAndSubject_SubjectCode(
                request.getStudentCode(), request.getSubjectCode());

        Score scoreToSave;
        String actionType;
        String oldValueStr = "N/A";

        if (existingScoreOpt.isPresent()) {
            actionType = "UPDATE_SCORE";
            Score existingScore = existingScoreOpt.get();
            oldValueStr = String.format("Process: %.1f, Final: %.1f",
                    existingScore.getProcessScore(), existingScore.getFinalScore());

            scoreToSave = existingScore;
            scoreToSave.setProcessScore(request.getProcessScore());
            scoreToSave.setFinalScore(request.getFinalScore());
        } else {
            // Trường hợp CREATE
            actionType = "CREATE_SCORE";
            scoreToSave = Score.builder()
                    .student(student)
                    .subject(subject)
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
    @Cacheable(value = "student_scores", key = "#studentCode")
    public TranscriptResponse getStudentTranscript(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Faculty faculty = student.getFaculty();
        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);
        List<ScoreDto> scoreDtos = scores.stream().map(scoreMapper::toDto).toList();

        if (faculty == null) {
            return new TranscriptResponse(student.getFullName(), "N/A",
                    student.getStatus().name(), "Chưa cập nhật thông tin Khoa", scoreDtos);
        }

        if (student.getStatus() == StudentStatus.GRADUATED) {
            return new TranscriptResponse(student.getFullName(), faculty.getFacultyName(),
                    "ĐÃ TỐT NGHIỆP", "Chúc mừng! Bạn đã hoàn thành chương trình đào tạo.", scoreDtos);
        }
        if (student.getStatus() == StudentStatus.EXPELLED) {
            return new TranscriptResponse(student.getFullName(), faculty.getFacultyName(),
                    "BUỘC THÔI HỌC", "Cảnh báo: Bạn đã bị buộc thôi học do quá thời gian đào tạo.", scoreDtos);
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
                scoreDtos
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "student_scores", allEntries = true)
    public void importScores(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File excel rỗng");
        }

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentCode = row.getCell(0).getStringCellValue();
                String subjectCode = row.getCell(1).getStringCellValue();
                double processScore = row.getCell(2).getNumericCellValue();
                double finalScore = row.getCell(3).getNumericCellValue();

                // Tạo DTO
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
