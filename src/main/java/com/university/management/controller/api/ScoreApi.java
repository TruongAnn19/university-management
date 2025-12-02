package com.university.management.controller.api;

import com.university.management.exception.ErrorResponse;
import com.university.management.model.dto.ScoreDto;
import com.university.management.model.dto.requestDto.ScoreRequestDto;
import com.university.management.model.dto.response.TranscriptResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/scores")
@Tag(name = "Score Management", description = "API quản lý điểm số sinh viên")
public interface ScoreApi {
    // Định nghĩa API Nhập điểm
    @Operation(summary = "Nhập điểm mới thủ công", description = "API dùng cho Giảng viên nhập điểm kết thúc học phần")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nhập điểm thành công",
                    content = @Content(schema = @Schema(implementation = ScoreDto.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ (Validation)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    ResponseEntity<ScoreDto> createScore(@Valid @RequestBody ScoreRequestDto request);


    // Định nghĩa API Xem điểm
    @Operation(summary = "Xem bảng điểm", description = "Lấy danh sách điểm của một sinh viên theo Mã SV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Sai mã sinh viên")
    })
    @GetMapping("/{studentCode}")
    @PreAuthorize("@customSecurity.isOwnerOrTeacher(#studentCode)")
    ResponseEntity<TranscriptResponse> getStudentScores(@PathVariable("studentCode") String studentCode);

    @Operation(summary = "Nhập điểm mới", description = "API dùng cho Giảng viên nhập điểm kết thúc học phần bằng file excel")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file);
}
