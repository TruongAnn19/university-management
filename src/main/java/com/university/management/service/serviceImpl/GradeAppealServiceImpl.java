package com.university.management.service.serviceImpl;

import com.university.management.mapper.AppealMapper;
import com.university.management.model.dto.requestDto.AppealRequest;
import com.university.management.model.dto.requestDto.AppealReviewRequest;
import com.university.management.model.dto.response.AppealResponse;
import com.university.management.model.entity.*;
import com.university.management.repository.GradeAppealRepository;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.service.GradeAppealService;
import com.university.management.service.NotificationService;
import com.university.management.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeAppealServiceImpl implements GradeAppealService {
    private final GradeAppealRepository appealRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;
    private final AppealMapper appealMapper;
    private final StudentService studentService;

    @Override
    @Transactional
    public void createAppeal(String studentCode, AppealRequest request) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("SV không tồn tại"));

        Score score = scoreRepository.findById(request.scoreId())
                .orElseThrow(() -> new RuntimeException("Điểm số không tồn tại"));

        if (!score.getStudent().getStudentCode().equals(studentCode)) {
            throw new RuntimeException("Bạn không thể phúc khảo điểm của người khác!");
        }

        List<GradeAppeal> existingAppealOpt = appealRepository.findByStudent_StudentCodeAndScore_Id(studentCode, request.scoreId());

        if (existingAppealOpt.isEmpty()) {
            GradeAppeal existingAppeal = existingAppealOpt.get(0);

            if (existingAppeal.getStatus() == AppealStatus.PENDING) {
                throw new RuntimeException("Bạn đã gửi đơn phúc khảo cho môn này rồi. Vui lòng chờ kết quả!");
            }
            else {
                String result = (existingAppeal.getStatus() == AppealStatus.APPROVED) ? "được CHẤP NHẬN" : "bị TỪ CHỐI";
                throw new RuntimeException("Bạn đã phúc khảo môn này và đơn đã " + result + ". Không thể gửi lại!");
            }
        }

        LocalDateTime scoreTime = score.getUpdatedAt();
        if (scoreTime != null) {
            LocalDateTime deadline = scoreTime.plusMonths(1);
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(deadline)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                throw new RuntimeException("Đã quá hạn phúc khảo! Điểm cập nhật ngày "
                        + scoreTime.format(formatter) + ". Hạn chót là " + deadline.format(formatter));
            }
        }

        GradeAppeal appeal = GradeAppeal.builder()
                .student(student)
                .score(score)
                .reason(request.reason())
                .status(AppealStatus.PENDING)
                .build();

        appealRepository.save(appeal);
    }

    @Override
    @Transactional
    public void reviewAppeal(Long appealId, AppealReviewRequest request) {
        GradeAppeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new RuntimeException("Đơn không tồn tại"));

        if (appeal.getStatus() != AppealStatus.PENDING) {
            throw new RuntimeException("Đơn này đã được xử lý rồi!");
        }

        appeal.setStatus(request.status());
        appeal.setTeacherResponse(request.teacherResponse());

        if (request.status() == AppealStatus.APPROVED) {
            if (request.newScore() == null) {
                throw new RuntimeException("Vui lòng nhập điểm mới khi Chấp nhận phúc khảo");
            }

            Score score = appeal.getScore();
            score.setFinalScore(request.newScore());
            scoreRepository.save(score);
            String studentCode = appeal.getStudent().getStudentCode();
            studentService.updateStudentGpa(studentCode);
        }

        GradeAppeal savedAppeal = appealRepository.save(appeal);
        User studentUser = savedAppeal.getStudent().getUser();
        String subjectName = savedAppeal.getScore().getSubject().getSubjectName();
        String result = (request.status() == AppealStatus.APPROVED) ? "Đơn phúc khảo môn " + subjectName + " đã được " + "CHẤP NHẬN" : "Đơn phúc khảo môn " + subjectName + " đã bị " + "TỪ CHỐI";

        notificationService.createNotification(
                studentUser,
                "KẾT QUẢ PHÚC KHẢO",
                result
        );
    }

    @Override
    public List<GradeAppeal> getMyAppeals(String studentCode) {
        return appealRepository.findByStudent_StudentCode(studentCode);
    }

    @Override
    @Transactional
    public List<AppealResponse> getPendingAppeals() {
        List<GradeAppeal> entities = appealRepository.findByStatus(AppealStatus.PENDING);
        return entities.stream()
                .map(appealMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeAppeal> getAppealsByStudent(String studentCode) {
        if (studentRepository.findByStudentCode(studentCode).isEmpty()) {
            throw new RuntimeException("Mã sinh viên không tồn tại: " + studentCode);
        }
        return appealRepository.findByStudent_StudentCode(studentCode);
    }
}
