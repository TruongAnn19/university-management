package com.university.management.mapper;

import com.university.management.model.dto.ScoreDto;
import com.university.management.model.entity.Score;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScoreMapper {
    @Mapping(source = "student.studentCode", target = "studentCode")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "subject.subjectName", target = "subjectName")
    @Mapping(source = "subject.credits", target = "credits")
    ScoreDto toDto(Score score);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "id", ignore = true)
    Score toEntity(ScoreDto dto);
}
