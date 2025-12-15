package com.university.management.service;

import com.university.management.model.dto.response.UserProfileResponse;

public interface UserProfileService {
    UserProfileResponse getMyProfile();
    UserProfileResponse getUserProfileById(Long userId);
}
