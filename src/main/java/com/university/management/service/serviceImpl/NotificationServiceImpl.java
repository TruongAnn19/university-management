package com.university.management.service.serviceImpl;

import com.university.management.model.dto.response.NotificationResponse;
import com.university.management.model.entity.Notification;
import com.university.management.model.entity.User;
import com.university.management.repository.NotificationRepository;
import com.university.management.repository.UserRepository;
import com.university.management.service.FcmService;
import com.university.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Override
    public void createNotification(User receiver, String title, String message) {
        Notification notification = Notification.builder()
                .user(receiver)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        fcmService.sendNotification(receiver.getFcmToken(), title, message);
    }

    @Override
    @Transactional
    public void updateFcmToken(String username, String token) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setFcmToken(token);
        userRepository.save(user);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String username) {
        return notificationRepository.findByUser_UsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(n -> new NotificationResponse(
                        n.getId(), n.getTitle(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
