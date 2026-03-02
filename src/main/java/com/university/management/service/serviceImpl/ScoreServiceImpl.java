package com.university.management.service.serviceImpl;

import com.university.management.mapper.ScoreMapper;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.dto.response.TranscriptResponse;
import com.university.management.model.entity.*;
import com.university.management.repository.*;
import com.university.management.service.AuditService;
import com.university.management.service.ScoreService;
import com.university.management.service.StudentService;
import com.university.management.util.StudentStatusHelper;
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
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final Logger logger = Logger.getLogger(ScoreServiceImpl.class.getName());

    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreMapper scoreMapper;
    private final AuditService auditService;
    private final SemesterRepository semesterRepository;
    private final RegistrationRepository registrationRepository;
    private final StudentService studentService;
    private final StudentStatusHelper statusHelper;

    @Override
    // @CacheEvict(value = "student_scores", key = "#request.studentCode()")
    public ScoreDto recordScore(ScoreRequestDto request) {
        String currentUsername = "SYSTEM";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(
                        () -> new RuntimeException("Lỗi: Không tìm thấy sinh viên mã " + request.getStudentCode()));

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
        studentService.updateStudentGpa(request.getStudentCode());
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
    // @Cacheable(value = "student_scores", key = "#studentCode")
    public TranscriptResponse getStudentTranscript(String studentCode) {
        // 1. Tìm thông tin sinh viên
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Faculty faculty = student.getFaculty();
        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);
        List<ScoreDto> scoresDto = scores.stream().map(scoreMapper::toDto).toList();

        // 2. Kiểm tra thông tin khoa (tránh lỗi Null khi tính toán)
        if (faculty == null) {
            return new TranscriptResponse(student.getFullName(), "N/A",
                    student.getStatus().name(), "Chưa cập nhật thông tin Khoa", scoresDto);
        }

        // 3. Sử dụng Helper để tính toán trạng thái và thông điệp mới nhất
        // Lấy năm hiện tại để đảm bảo tính nhất quán
        int currentYear = LocalDate.now().getYear();
        StudentStatusHelper.StudentStatusResult result = statusHelper.determineStatus(student, scores, currentYear);

        // 4. Cập nhật trạng thái vào Database nếu có thay đổi
        // Điều này giúp đồng bộ dữ liệu ngay cả khi sinh viên chỉ vào "xem" bảng điểm
        if (student.getStatus() != result.status()) {
            student.setStatus(result.status());
            studentRepository.save(student);
        }

        // 5. Trả về Response với thông tin từ Helper
        return new TranscriptResponse(
                student.getFullName(),
                faculty.getFacultyName(),
                result.status().name(),
                result.message(),
                scoresDto);
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
    // @CacheEvict(value = "student_scores", allEntries = true)
    public void importScores(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File excel rỗng");
        }

        Semester currentSemester = semesterRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Không có học kỳ nào đang mở đăng ký!"));

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            // 1. Quét 1 vòng Excel, gom hết các Mã SV và Mã Môn Học
            java.util.Set<String> studentCodes = new java.util.HashSet<>();
            java.util.Set<String> subjectCodes = new java.util.HashSet<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;
                String studentCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectCode = dataFormatter.formatCellValue(row.getCell(1)).trim();
                if (!studentCode.isEmpty() && !subjectCode.isEmpty()) {
                    studentCodes.add(studentCode);
                    subjectCodes.add(subjectCode);
                }
            }

            if (studentCodes.isEmpty())
                return;

            // 2. Truy vấn dữ liệu một lần (Batch Select)
            java.util.Map<String, Student> studentMap = studentRepository.findByStudentCodeIn(studentCodes)
                    .stream().collect(java.util.stream.Collectors.toMap(Student::getStudentCode, s -> s));

            java.util.Map<String, Subject> subjectMap = subjectRepository.findBySubjectCodeIn(subjectCodes)
                    .stream().collect(java.util.stream.Collectors.toMap(Subject::getSubjectCode, s -> s));

            List<Registration> bulkRegistrations = registrationRepository.findByStudent_StudentCodeIn(studentCodes);

            List<Score> existingScores = scoreRepository.findByStudent_StudentCodeInAndSemester(studentCodes,
                    currentSemester);

            java.util.Map<String, Score> existingScoreMap = existingScores.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            s -> s.getStudent().getStudentCode() + "_" + s.getSubject().getSubjectCode(),
                            s -> s,
                            (s1, s2) -> s1));

            List<Score> scoresToSave = new java.util.ArrayList<>();
            java.util.Set<String> updatedStudentCodes = new java.util.HashSet<>();

            String currentUsername = "SYSTEM";
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            }

            // 3. Lặp qua Excel lần 2 xử lý logic và tạo List
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                String studentCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectCode = dataFormatter.formatCellValue(row.getCell(1)).trim();

                if (studentCode.isEmpty() || subjectCode.isEmpty())
                    continue;

                Student student = studentMap.get(studentCode);
                Subject subject = subjectMap.get(subjectCode);

                if (student == null)
                    throw new RuntimeException("Không tìm thấy sinh viên mã " + studentCode + " tại dòng " + (i + 1));
                if (subject == null)
                    throw new RuntimeException("Không tìm thấy môn học mã " + subjectCode + " tại dòng " + (i + 1));

                String mapKey = studentCode + "_" + subjectCode;

                if (existingScoreMap.containsKey(mapKey)) {
                    continue; // Bỏ qua nếu đã có điểm
                }

                // Kiểm tra đăng ký môn học
                List<Registration> studentRegs = bulkRegistrations.stream()
                        .filter(r -> r.getStudent().getStudentCode().equals(studentCode)
                                && r.getCourseClass().getSubject().getSubjectCode().equals(subjectCode))
                        .toList();

                if (studentRegs.isEmpty()) {
                    throw new RuntimeException(
                            studentCode + " CHƯA TỪNG ĐĂNG KÝ môn " + subjectCode + ". Không thể nhập điểm!");
                }

                boolean isStudying = studentRegs.stream()
                        .anyMatch(r -> Boolean.TRUE.equals(r.getCourseClass().getSemester().getIsActive()));

                if (isStudying) {
                    throw new RuntimeException("Sinh viên đang trong thời gian học môn " + subjectCode
                            + ". Vui lòng đợi kết thúc học kỳ để nhập điểm.");
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

                Score scoreToSave = Score.builder()
                        .student(student)
                        .subject(subject)
                        .semester(currentSemester)
                        .processScore(processScore)
                        .finalScore(finalScore)
                        .build();
                scoreToSave.calculateTotal(); // Explicit manual calculation before saves

                scoresToSave.add(scoreToSave);
                updatedStudentCodes.add(studentCode);
            }

            // 4. Lưu Batch
            if (!scoresToSave.isEmpty()) {
                List<Score> savedScores = scoreRepository.saveAll(scoresToSave);

                // Ghi log và update GPA
                for (Score s : savedScores) {
                    String newValueStr = String.format("Process: %.1f, Final: %.1f", s.getProcessScore(),
                            s.getFinalScore());
                    auditService.logAction("CREATE_SCORE", "SCORE", s.getId().toString(), "N/A", newValueStr,
                            currentUsername);
                }

                for (String stuCode : updatedStudentCodes) {
                    studentService.updateStudentGpa(stuCode);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi dữ liệu tại dòng đang xử lý: " + e.getMessage());
        }
    }

}
