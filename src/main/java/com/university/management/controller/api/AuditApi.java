package com.university.management.controller.api;

import com.university.management.model.entity.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/audit-logs")
@Tag(name = "System Audit", description = "Nhật ký hệ thống")
public interface AuditApi {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xem toàn bộ nhật ký hoạt động")
    ResponseEntity<List<AuditLog>> getAllLogs();
}
