package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_classes")
@SQLDelete(sql = "UPDATE course_classes SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class CourseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String classCode;

    @Column(name = "max_slot")
    private Integer maxSlot;

    @Column(name = "current_slot")
    private Integer currentSlot;

    // CƠ CHẾ CHỐNG TRANH CHẤP DỮ LIỆU
    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted = false;
}
