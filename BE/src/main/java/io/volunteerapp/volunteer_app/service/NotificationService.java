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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final CloudinaryService cloudinaryService;
    private final io.volunteerapp.volunteer_app.repository.UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                              UserNotificationRepository userNotificationRepository,
                              EventRepository eventRepository,
                              EventRegistrationRepository eventRegistrationRepository,
                              CloudinaryService cloudinaryService,
                              io.volunteerapp.volunteer_app.repository.UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.eventRepository = eventRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
    }

    // Lấy notification theo ID và tự động đánh dấu đã đọc
    @Transactional
    public NotificationResponse getNotificationById(Integer id, Integer userId) {
        System.out.println("🔍 getNotificationById called - notificationId: " + id + ", userId: " + userId);
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        // Tự động đánh dấu đã đọc nếu chưa đọc
        if (userId != null) {
            System.out.println("👤 Looking for UserNotification with userId=" + userId + " and notificationId=" + id);
            
            UserNotification userNotification = userNotificationRepository
                    .findByUserIdAndNotificationId(userId, id)
                    .orElse(null);
            
            if (userNotification != null) {
                System.out.println("✅ Found UserNotification - ID: " + userNotification.getId() + ", isRead: " + userNotification.getIsRead());
                
                if (!userNotification.getIsRead()) {
                    System.out.println("📝 Marking as read...");
                    userNotification.setIsRead(true);
                    userNotification.setReadAt(Instant.now());
                    userNotificationRepository.save(userNotification);
                    System.out.println("✅ Successfully marked as read");
                } else {
                    System.out.println("ℹ️ Already marked as read");
                }
            } else {
                System.out.println("❌ UserNotification not found! Creating new UserNotification...");
                // Tự động tạo UserNotification mới và đánh dấu là đã đọc
                io.volunteerapp.volunteer_app.model.User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                
                UserNotification newUserNotification = new UserNotification();
                newUserNotification.setUser(user);
                newUserNotification.setNotification(notification);
                newUserNotification.setIsRead(true);
                newUserNotification.setReadAt(Instant.now());
                newUserNotification.setCreatedAt(Instant.now());
                
                userNotificationRepository.save(newUserNotification);
                System.out.println("✅ Created new UserNotification and marked as read");
            }
        } else {
            System.out.println("⚠️ userId is null, skipping mark as read");
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

    // Upload attachment và trả về URL
    public String uploadAttachment(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        System.out.println("🔄 Đang upload file lên Cloudinary...");
        String url = cloudinaryService.uploadAttachment(file);
        
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("Không lấy được URL từ Cloudinary");
        }
        
        System.out.println("✅ Upload thành công! URL: " + url);
        return url;
    }

    // Gửi notification đến người dùng đã đăng ký sự kiện
    public int sendNotificationToEventParticipants(SendNotificationRequest request) {
        // URL đã được upload trước và truyền vào qua request.getAttachmentUrl()
        System.out.println("📝 Bắt đầu lưu notification vào database...");
        String attachmentUrl = request.getAttachmentUrl();
        
        if (attachmentUrl != null && !attachmentUrl.trim().isEmpty()) {
            System.out.println("📎 Attachment URL: " + attachmentUrl);
        }
        
        return saveNotificationToDatabase(request, attachmentUrl);
    }

    // Method riêng với @Transactional để lưu vào DB sau khi đã có URL
    @Transactional
    private int saveNotificationToDatabase(SendNotificationRequest request, String attachmentUrl) {
        // Get event
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện với id: " + request.getEventId()));
        
        // Get registrations based on recipient type
        List<EventRegistration> registrations;
        String recipientType = request.getRecipientType() != null ? request.getRecipientType() : "ALL";

        if ("CREATOR".equals(recipientType)) {
            // Create notification for the event creator (Organization)
            Notification notification = new Notification();
            notification.setTitle(request.getTitle());
            notification.setContent(request.getContent());
            notification.setAttached(attachmentUrl);
            notification.setSenderRole("ADMIN");
            notification.setType("SYSTEM");
            notification.setCreatedAt(Instant.now());
            
            Notification savedNotification = notificationRepository.save(notification);
            System.out.println("✅ Notification đã lưu - ID: " + savedNotification.getId() + ", Attached: " + savedNotification.getAttached());

            UserNotification userNotification = new UserNotification();
            userNotification.setUser(event.getCreator());
            userNotification.setNotification(savedNotification);
            userNotification.setIsRead(false);
            userNotification.setCreatedAt(Instant.now());
            
            userNotificationRepository.save(userNotification);
            return 1;
        }
        
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
            System.out.println("⚠️ Không có người đăng ký nào");
            return 0;
        }
        
        // Create notification
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setAttached(attachmentUrl);
        notification.setSenderRole("ORGANIZATION");
        notification.setType("ORGANIZATION");
        notification.setCreatedAt(Instant.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        System.out.println("✅ Notification đã lưu - ID: " + savedNotification.getId() + ", Attached: " + savedNotification.getAttached());
        
        // Create user notifications for each participant
        int sentCount = 0;
        Integer eventRewardPoints = event.getRewardPoints();
        
        for (EventRegistration registration : registrations) {
            UserNotification userNotification = new UserNotification();
            userNotification.setUser(registration.getUser());
            userNotification.setNotification(savedNotification);
            userNotification.setIsRead(false);
            userNotification.setCreatedAt(Instant.now());
            
            userNotificationRepository.save(userNotification);
            
            // Cộng reward points cho user nếu event có reward points
            if (eventRewardPoints != null && eventRewardPoints > 0) {
                io.volunteerapp.volunteer_app.model.User user = registration.getUser();
                user.setTotalPoints(user.getTotalPoints() + eventRewardPoints);
                userRepository.save(user);
                System.out.println("✅ Đã cộng " + eventRewardPoints + " điểm cho user: " + user.getFullName());
            }
            
            sentCount++;
        }
        
        System.out.println("✅ Đã gửi notification cho " + sentCount + " người dùng");
        return sentCount;
    }

    @Transactional
    public void sendSystemNotification(Integer userId, String title, String content) {
        // Create Notification
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderRole("SYSTEM");
        notification.setType("PERSONAL");
        notification.setCreatedAt(Instant.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Create UserNotification
        UserNotification userNotification = new UserNotification();
        userNotification.setUser(new io.volunteerapp.volunteer_app.model.User());
        userNotification.getUser().setId(userId); // Set ID directly to avoid fetching if possible, or fetch if needed
        userNotification.setNotification(savedNotification);
        userNotification.setIsRead(false);
        userNotification.setCreatedAt(Instant.now());
        
        userNotificationRepository.save(userNotification);
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

