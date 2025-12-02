package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grade_appeals")
public class GradeAppeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "teacher_response", columnDefinition = "TEXT")
    private String teacherResponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppealStatus status;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = AppealStatus.PENDING;
    }
}
