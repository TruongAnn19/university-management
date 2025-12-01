package com.university.management.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_classes")
public class CourseClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String classCode;

    private Integer maxSlot;
    private Integer currentSlot;

    // CƠ CHẾ CHỐNG TRANH CHẤP DỮ LIỆU
    @Version
    private Long version;
}
