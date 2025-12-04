package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scores")
@SQLDelete(sql = "UPDATE scores SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
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
        this.totalScore = (processScore * pPercent) + (finalScore * fPercent);
    }

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;
}
