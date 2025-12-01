package com.university.management.service.serviceImpl;

import com.university.management.mapper.ScoreMapper;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.entity.Score;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.Subject;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.SubjectRepository;
import com.university.management.service.ScoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreMapper scoreMapper;

    @Override
    @CacheEvict(value = "student_scores", key = "#request.studentCode()")
    public ScoreDto recordScore(ScoreRequestDto request) {
        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sinh viên mã " + request.getStudentCode()));

        Subject subject = subjectRepository.findBySubjectCode(request.getSubjectCode())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy môn học mã " + request.getSubjectCode()));

        Score score = Score.builder()
                .student(student)
                .subject(subject)
                .processScore(request.getProcessScore())
                .finalScore(request.getFinalScore())
                .build();

        Score savedScore = scoreRepository.save(score);

        return scoreMapper.toDto(savedScore);
    }

    @Override
    @Cacheable(value = "student_scores", key = "#studentCode")
    public List<ScoreDto> getStudentTranscript(String studentCode) {
        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);

        return scores.stream().map(scoreMapper::toDto).toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "student_scores", allEntries = true)
    public void importScores(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File excel rỗng");
        }

        try (var inputStream = file.getInputStream();
             var workbook = new XSSFWorkbook(inputStream)) {

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

                // Tái sử dụng hàm recordScore
                this.recordScore(requestDto);
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi dữ liệu tại dòng đang xử lý: " + e.getMessage());
        }
    }

}
