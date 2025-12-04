package com.university.management.service.serviceImpl;

import com.university.management.model.entity.Subject;
import com.university.management.repository.SubjectRepository;
import com.university.management.service.SubjectService;
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
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public void importSubjects(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String subjectCode = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String subjectName = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String creditsStr = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String processPercentStr = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String finalPercentStr = dataFormatter.formatCellValue(row.getCell(4)).trim();

                if (subjectCode.isEmpty() || subjectName.isEmpty()) continue;

                if (subjectRepository.findBySubjectCode(subjectCode).isPresent()) {
                    continue;
                }

                int credits = creditsStr.isEmpty() ? 0 : Integer.parseInt(creditsStr);
                int processPercent = processPercentStr.isEmpty() ? 30 : Integer.parseInt(processPercentStr);
                int finalPercent = finalPercentStr.isEmpty() ? 70 : Integer.parseInt(finalPercentStr);

                Subject subject = Subject.builder()
                        .subjectCode(subjectCode)
                        .subjectName(subjectName)
                        .credits(credits)
                        .processPercent(processPercent)
                        .finalPercent(finalPercent)
                        .build();

                subjectRepository.save(subject);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import môn học: " + e.getMessage());
        }
    }
}
