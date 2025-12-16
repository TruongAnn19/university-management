package com.university.management.service.serviceImpl;

import com.university.management.mapper.ProfileMapper;
import com.university.management.model.dto.response.UserProfileResponse;
import com.university.management.model.entity.User;
import com.university.management.repository.StudentRepository;
import com.university.management.repository.TeacherRepository;
import com.university.management.repository.UserRepository;
import com.university.management.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return buildProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        return buildProfileResponse(user);
    }

    private UserProfileResponse buildProfileResponse(User user) {
        UserProfileResponse response = profileMapper.toResponse(user);

        switch (user.getRole()) {
            case STUDENT:
                studentRepository.findByUserId(user.getId())
                        .ifPresent(student -> response.setDetails(profileMapper.toStudentDetails(student)));
                break;
            case TEACHER:
                teacherRepository.findByUserId(user.getId())
                        .ifPresent(teacher -> response.setDetails(profileMapper.toTeacherDetails(teacher)));
                break;
            case ADMIN:
                break;
        }
        return response;
    }
}
