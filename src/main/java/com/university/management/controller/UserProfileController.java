package com.university.management.controller;

import com.university.management.controller.api.UserProfileApi;
import com.university.management.model.dto.response.UserProfileResponse;
import com.university.management.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserProfileController  implements UserProfileApi {
    private final UserProfileService userProfileService;

    @Override
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userProfileService.getMyProfile());
    }

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfileById(Long userId) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(userId));
    }
}
