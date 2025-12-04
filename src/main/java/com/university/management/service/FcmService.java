package com.university.management.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmService {
    @Async
    public void sendNotification(String targetToken, String title, String body) {
        if (targetToken == null || targetToken.isEmpty()) {
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(targetToken)
                    .setNotification(notification)
                    .build();

            FirebaseMessaging.getInstance().send(message);
            log.info("FCM Sent to: {}", targetToken);
        } catch (Exception e) {
            log.error("FCM Error: {}", e.getMessage());
        }
    }
}
