package com.university.management.mapper;

import com.university.management.model.dto.StudentDetailsDTO;
import com.university.management.model.dto.TeacherDetailsDTO;
import com.university.management.model.dto.response.UserProfileResponse;
import com.university.management.model.entity.Student;
import com.university.management.model.entity.Teacher;
import com.university.management.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(target = "details", ignore = true)
    UserProfileResponse toResponse(User user);
    StudentDetailsDTO toStudentDetails(Student student);
    TeacherDetailsDTO toLecturerDetails(Teacher teacher);
}
