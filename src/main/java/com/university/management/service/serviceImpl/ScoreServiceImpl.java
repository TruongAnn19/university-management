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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public List<ScoreDto> getStudentTranscript(String studentCode) {
        List<Score> scores = scoreRepository.findByStudent_StudentCode(studentCode);

        return scores.stream()
                .map(scoreMapper::toDto)
                .collect(Collectors.toList());
    }
}
