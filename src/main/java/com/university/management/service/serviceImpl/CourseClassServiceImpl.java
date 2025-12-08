package com.university.management.service.serviceImpl;

import com.university.management.model.entity.CourseClass;
import com.university.management.model.entity.Semester;
import com.university.management.model.entity.Subject;
import com.university.management.repository.CourseClassRepository;
import com.university.management.repository.SemesterRepository;
import com.university.management.repository.SubjectRepository;
import com.university.management.service.CourseClassService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor

public class CourseClassServiceImpl implements CourseClassService {
    private final CourseClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    @Override
    @Transactional
    public void importClasses(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        Semester currentSemester = semesterRepository.findByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Chưa có học kỳ nào được kích hoạt!"));

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                int currentRowIndex = i;

                String classCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectCode = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String maxSlotStr = dataFormatter.formatCellValue(row.getCell(2)).trim();

                if (classCode.isEmpty() || subjectCode.isEmpty()) continue;

                Subject subject = subjectRepository.findBySubjectCode(subjectCode)
                        .orElseThrow(() -> new RuntimeException("Mã môn " + subjectCode + " không tồn tại (Dòng " + (currentRowIndex + 1) + ")"));

                if (classRepository.findByClassCode(classCode).isPresent()) continue;

                int maxSlot = maxSlotStr.isEmpty() ? 60 : Integer.parseInt(maxSlotStr);

                CourseClass courseClass = CourseClass.builder()
                        .classCode(classCode)
                        .subject(subject)
                        .semester(currentSemester)
                        .maxSlot(maxSlot)
                        .currentSlot(0)
                        .build();

                classRepository.save(courseClass);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import lớp: " + e.getMessage());
        }
    }
}
