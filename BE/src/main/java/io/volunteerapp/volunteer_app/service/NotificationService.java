package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.requeset.SendNotificationRequest;
import io.volunteerapp.volunteer_app.DTO.response.NotificationResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserNotificationResponse;
import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.EventRegistration;
import io.volunteerapp.volunteer_app.model.Notification;
import io.volunteerapp.volunteer_app.model.UserNotification;
import io.volunteerapp.volunteer_app.repository.EventRegistrationRepository;
import io.volunteerapp.volunteer_app.repository.EventRepository;
import io.volunteerapp.volunteer_app.repository.NotificationRepository;
import io.volunteerapp.volunteer_app.repository.UserNotificationRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;

    public NotificationService(NotificationRepository notificationRepository,
                              UserNotificationRepository userNotificationRepository,
                              EventRepository eventRepository,
                              EventRegistrationRepository eventRegistrationRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
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

    // Gửi notification đến người dùng đã đăng ký sự kiện
    @Transactional
    public int sendNotificationToEventParticipants(SendNotificationRequest request) {
        // Get event
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + request.getEventId()));
        
        // Get registrations based on recipient type
        List<EventRegistration> registrations;
        String recipientType = request.getRecipientType() != null ? request.getRecipientType() : "ALL";
        
        if ("APPROVED".equals(recipientType)) {
            registrations = eventRegistrationRepository.findByEventAndStatus(event, "APPROVED", 
                Pageable.unpaged()).getContent();
        } else if ("PENDING".equals(recipientType)) {
            registrations = eventRegistrationRepository.findByEventAndStatus(event, "PENDING", 
                Pageable.unpaged()).getContent();
        } else {
            // ALL
            registrations = eventRegistrationRepository.findByEvent(event, 
                Pageable.unpaged()).getContent();
        }
        
        if (registrations.isEmpty()) {
            return 0;
        }
        
        // Create notification
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setType("ORGANIZATION");
        notification.setCreatedAt(Instant.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Create user notifications for each participant
        int sentCount = 0;
        for (EventRegistration registration : registrations) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(registration.getUser());
            userNotification.setNotification(savedNotification);
            userNotification.setIsRead(false);
            userNotification.setCreatedAt(Instant.now());
            
            userNotificationRepository.save(userNotification);
            sentCount++;
        }
        
        return sentCount;
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

