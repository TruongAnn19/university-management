package com.university.management.service.serviceImpl;

import com.university.management.model.dto.requestDto.AppealRequest;
import com.university.management.model.dto.requestDto.AppealReviewRequest;
import com.university.management.model.entity.AppealStatus;
import com.university.management.model.entity.GradeAppeal;
import com.university.management.model.entity.Score;
import com.university.management.model.entity.Student;
import com.university.management.repository.GradeAppealRepository;
import com.university.management.repository.ScoreRepository;
import com.university.management.repository.StudentRepository;
import com.university.management.service.GradeAppealService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeAppealServiceImpl implements GradeAppealService {
    private final GradeAppealRepository appealRepository;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void createAppeal(String studentCode, AppealRequest request) {
        // 1. Tìm SV
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("SV không tồn tại"));

        // 2. Tìm điểm số cần phúc khảo
        Score score = scoreRepository.findById(request.scoreId())
                .orElseThrow(() -> new RuntimeException("Điểm số không tồn tại"));

        // 3. Check xem điểm này có phải của SV này không (Bảo mật)
        if (!score.getStudent().getStudentCode().equals(studentCode)) {
            throw new RuntimeException("Bạn không thể phúc khảo điểm của người khác!");
        }

        // 4. Lưu đơn
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
        }

        appealRepository.save(appeal);
    }

    @Override
    public List<GradeAppeal> getMyAppeals(String studentCode) {
        return appealRepository.findByStudent_StudentCode(studentCode);
    }

    @Override
    public List<GradeAppeal> getPendingAppeals() {
        return appealRepository.findByStatus(AppealStatus.PENDING);
    }

    @Override
    public List<GradeAppeal> getAppealsByStudent(String studentCode) {
        if (studentRepository.findByStudentCode(studentCode).isEmpty()) {
            throw new RuntimeException("Mã sinh viên không tồn tại: " + studentCode);
        }
        return appealRepository.findByStudent_StudentCode(studentCode);
    }
}
