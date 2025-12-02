package com.university.management.service.serviceImpl;

import com.university.management.model.entity.AuditLog;
import com.university.management.repository.AuditLogRepository;
import com.university.management.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {
    private final AuditLogRepository auditLogRepository;

    @Override
    @Async // <--- QUAN TRỌNG: Chạy ở thread riêng, không làm chậm request chính
    public void logAction(String action, String entityName, String entityId, Object oldVal, Object newVal,  String actor) {
        try {
            String finalActor = (actor != null) ? actor : "SYSTEM";

            AuditLog logEntry = AuditLog.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .oldValue(oldVal == null ? "N/A" : oldVal.toString())
                    .newValue(newVal == null ? "N/A" : newVal.toString())
                    .actor(finalActor)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(logEntry);
            log.info("AUDIT LOG SAVED: {} by {}", action, finalActor);

        } catch (Exception e) {
            log.error("Lỗi ghi log: {}", e.getMessage());
        }
    }
}
