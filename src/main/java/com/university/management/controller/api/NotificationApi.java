package com.university.management.controller.api;

import com.university.management.model.dto.requestDto.FcmTokenRequest;
import com.university.management.model.dto.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/notifications")
@Tag(name = "Notification System", description = "Hệ thống thông báo (DB + Firebase)")
public interface NotificationApi {
    @Operation(summary = "Cập nhật FCM Token (Khi User mở App/Web)")
    @PostMapping("/fcm-token")
    ResponseEntity<String> updateFcmToken(@RequestBody FcmTokenRequest request);

    @Operation(summary = "Xem danh sách thông báo")
    @GetMapping
    ResponseEntity<List<NotificationResponse>> getMyNotifications();

    @Operation(summary = "Đánh dấu đã đọc")
    @PutMapping("/{id}/read")
    ResponseEntity<String> markAsRead(@PathVariable Long id);
}
