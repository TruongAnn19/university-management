package com.university.management.controller.api;

import com.university.management.model.dto.response.UserProfileResponse;
import com.university.management.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/profiles")
@Tag(name = "Profile Controller", description = "APIs for user profiles")
public interface UserProfileApi {
    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    ResponseEntity<UserProfileResponse> getMyProfile();

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user profile by ID (Admin only)")
    ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long userId);
}
