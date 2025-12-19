package com.university.management.mapper;

import com.university.management.model.dto.StudentDto;
import com.university.management.model.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(source = "faculty.facultyName", target = "facultyName")
    StudentDto toDto(Student student);

    List<StudentDto> toDtoList(List<Student> students);
}
