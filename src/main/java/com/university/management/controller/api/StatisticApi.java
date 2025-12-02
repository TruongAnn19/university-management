package com.university.management.controller.api;

import com.university.management.model.dto.response.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/statistics")
@Tag(name = "Statistics & Dashboard", description = "Thống kê báo cáo")
public interface StatisticApi {
    @Operation(summary = "Lấy dữ liệu tổng quan cho Dashboard")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<DashboardResponse> getDashboard();
}
