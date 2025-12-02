package com.university.management.controller;

import com.university.management.controller.api.AuditApi;
import com.university.management.model.entity.AuditLog;
import com.university.management.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuditController implements AuditApi {
    private final AuditLogRepository auditLogRepository;

    @Override
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
    }
}
