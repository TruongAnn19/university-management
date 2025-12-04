package com.university.management.controller;

import com.university.management.controller.api.NotificationApi;
import com.university.management.model.dto.requestDto.FcmTokenRequest;
import com.university.management.model.dto.response.NotificationResponse;
import com.university.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {
    private final NotificationService notificationService;

    @Override
    public ResponseEntity<String> updateFcmToken(FcmTokenRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.updateFcmToken(username, request.token());
        return ResponseEntity.ok("Đã cập nhật Token thiết bị.");
    }

    @Override
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(notificationService.getMyNotifications(username));
    }

    @Override
    public ResponseEntity<String> markAsRead(Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Đã đánh dấu đã đọc.");
    }
}
