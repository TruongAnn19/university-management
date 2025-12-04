package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action")
    private String action;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private String entityId;

    @Column(columnDefinition = "TEXT", name = "old_value")
    private String oldValue;

    @Column(columnDefinition = "TEXT", name = "new_value")
    private String newValue;

    @Column(name = "actor")
    private String actor;

    @Column(name = "times_tamp")
    private LocalDateTime timestamp;
}
