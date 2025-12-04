package com.university.management.service;

public interface AuditService {
    void logAction(String action, String entityName, String entityId, Object oldVal, Object newVal, String actor);
}
