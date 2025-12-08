package com.university.management.service.serviceImpl;

import com.university.management.model.entity.Semester;
import com.university.management.repository.SemesterRepository;
import com.university.management.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;

    @Override
    @Transactional
    public void importSemesters(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File rỗng");

        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            // Duyệt từ dòng 1 (bỏ tiêu đề)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String code = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String startDateStr = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String endDateStr = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String activeStr = dataFormatter.formatCellValue(row.getCell(4)).trim();

                if (code.isEmpty()) continue;

                if (semesterRepository.findBySemesterCode(code).isPresent()) {
                    continue;
                }

                LocalDate startDate = null;
                LocalDate endDate = null;
                try {
                    startDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    endDate = LocalDate.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } catch (Exception e) {
                    throw new RuntimeException("Lỗi định dạng ngày tại dòng " + (i + 1) + ". Yêu cầu: yyyy-MM-dd");
                }

                boolean isActive = "1".equals(activeStr) || "true".equalsIgnoreCase(activeStr);

                if (isActive) {
                    semesterRepository.findByIsActiveTrue().ifPresent(s -> {
                        s.setIsActive(false);
                        semesterRepository.save(s);
                    });
                }

                // 6. Lưu
                Semester semester = Semester.builder()
                        .semesterName(name)
                        .semesterCode(code)
                        .startDate(startDate)
                        .endDate(endDate)
                        .isActive(isActive)
                        .build();

                semesterRepository.save(semester);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi import học kỳ: " + e.getMessage());
        }
    }
}