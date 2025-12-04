package com.university.management.model.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction; // Thay cho @Where
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
@Entity
@Table(name = "semesters")
@SQLDelete(sql = "UPDATE semesters SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "semester_name")
    private String semesterName;

    @Column(unique = true, name = "semester_code")
    private String semesterCode;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "and_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;
}
