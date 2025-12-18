package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scores")
@SQLDelete(sql = "UPDATE scores SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
@EntityListeners(AuditingEntityListener.class)
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "process_score")
    private Double processScore;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (processScore == null || finalScore == null) {
            return;
        }
        double pPercent = 0.3;
        double fPercent = 0.7;

        if (subject != null) {
            if (subject.getProcessPercent() != null) {
                pPercent = subject.getProcessPercent() / 100.0;
            }
            if (subject.getFinalPercent() != null) {
                fPercent = subject.getFinalPercent() / 100.0;
            }
        }
        double total = (processScore * pPercent) + (finalScore * fPercent);
        this.totalScore = BigDecimal
                .valueOf(total)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
