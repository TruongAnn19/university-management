package com.university.management.controller;

import com.university.management.controller.api.StatisticApi;
import com.university.management.model.dto.response.DashboardResponse;
import com.university.management.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticController implements StatisticApi {
    private final StatisticService statisticService;

    @Override
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(statisticService.getDashboardData());
    }
}
