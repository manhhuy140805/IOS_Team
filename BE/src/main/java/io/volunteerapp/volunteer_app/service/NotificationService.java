package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.response.NotificationResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserNotificationResponse;
import io.volunteerapp.volunteer_app.model.Notification;
import io.volunteerapp.volunteer_app.model.UserNotification;
import io.volunteerapp.volunteer_app.repository.NotificationRepository;
import io.volunteerapp.volunteer_app.repository.UserNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;

    public NotificationService(NotificationRepository notificationRepository,
                              UserNotificationRepository userNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
    }

    // Lấy notification theo ID và tự động đánh dấu đã đọc
    @Transactional
    public NotificationResponse getNotificationById(Integer id, Integer userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        // Tự động đánh dấu đã đọc nếu chưa đọc
        if (userId != null) {
            UserNotification userNotification = userNotificationRepository
                    .findByUserIdAndNotificationId(userId, id)
                    .orElse(null);
            
            if (userNotification != null && !userNotification.getIsRead()) {
                userNotification.setIsRead(true);
                userNotification.setReadAt(Instant.now());
                userNotificationRepository.save(userNotification);
            }
        }
        
        return toNotificationResponse(notification);
    }

    // Lấy tất cả notifications của user
    public List<UserNotificationResponse> getUserNotifications(Integer userId) {
        List<UserNotification> userNotifications = userNotificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        
        return userNotifications.stream()
                .map(this::toUserNotificationResponse)
                .collect(Collectors.toList());
    }

    // Lấy notifications chưa đọc của user
    public List<UserNotificationResponse> getUnreadNotifications(Integer userId) {
        List<UserNotification> userNotifications = userNotificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        return userNotifications.stream()
                .map(this::toUserNotificationResponse)
                .collect(Collectors.toList());
    }

    // Lấy notifications đã đọc của user
    public List<UserNotificationResponse> getReadNotifications(Integer userId) {
        List<UserNotification> userNotifications = userNotificationRepository
                .findByUserIdAndIsReadTrueOrderByCreatedAtDesc(userId);
        
        return userNotifications.stream()
                .map(this::toUserNotificationResponse)
                .collect(Collectors.toList());
    }


    // Đánh dấu tất cả notifications là đã đọc
    @Transactional
    public void markAllAsRead(Integer userId) {
        userNotificationRepository.markAllAsReadByUserId(userId);
    }

    // Xóa một notification của user
    @Transactional
    public void deleteUserNotification(Integer userId, Integer notificationId) {
        UserNotification userNotification = userNotificationRepository
                .findByUserIdAndNotificationId(userId, notificationId)
                .orElseThrow(() -> new RuntimeException("UserNotification not found"));
        
        userNotificationRepository.delete(userNotification);
    }

    // Helper methods để convert entity sang response
    private NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getAttached(),
                notification.getSender() != null ? notification.getSender().getId() : null,
                notification.getSender() != null ? notification.getSender().getFullName() : null,
                notification.getSenderRole(),
                notification.getType(),
                notification.getCreatedAt()
        );
    }

    private UserNotificationResponse toUserNotificationResponse(UserNotification userNotification) {
        return new UserNotificationResponse(
                userNotification.getId(),
                userNotification.getUser().getId(),
                toNotificationResponse(userNotification.getNotification()),
                userNotification.getIsRead(),
                userNotification.getReadAt(),
                userNotification.getCreatedAt()
        );
    }
}

