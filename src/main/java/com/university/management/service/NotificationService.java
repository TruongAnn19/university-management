package com.university.management.service;

import com.university.management.model.dto.response.NotificationResponse;
import com.university.management.model.entity.User;

import java.util.List;

public interface NotificationService {
    void createNotification(User receiver, String title, String message);
    void updateFcmToken(String username, String token);
    List<NotificationResponse> getMyNotifications(String username);
    void markAsRead(Long id);
}
