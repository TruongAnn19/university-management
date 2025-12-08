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
@Table(name = "subjects")
@SQLDelete(sql = "UPDATE subjects SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", unique = true, nullable = false)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @Column(columnDefinition = "int default 30")
    private Integer processPercent = 30;

    @Column(columnDefinition = "int default 70")
    private Integer finalPercent = 70;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}
