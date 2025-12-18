package com.university.management.mapper;

import com.university.management.model.dto.response.AppealResponse;
import com.university.management.model.entity.GradeAppeal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppealMapper {
    @Mapping(source = "student.studentCode", target = "studentCode")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "score.subject.subjectName", target = "subjectName")
    @Mapping(source = "score.finalScore", target = "oldScore")
    AppealResponse toResponse(GradeAppeal appeal);
}
