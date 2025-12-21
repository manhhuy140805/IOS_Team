package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.response.NotificationResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserNotificationResponse;
import io.volunteerapp.volunteer_app.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Lấy notification theo ID và tự động đánh dấu đã đọc
     * GET /api/v1/notifications/{id}?userId={userId}
     */
    @GetMapping("/{id}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        NotificationResponse response = notificationService.getNotificationById(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy tất cả notifications của user
     * GET /api/v1/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserNotificationResponse>> getUserNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Lấy notifications chưa đọc của user
     * GET /api/v1/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserNotificationResponse>> getUnreadNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Lấy notifications đã đọc của user
     * GET /api/v1/notifications/user/{userId}/read
     */
    @GetMapping("/user/{userId}/read")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserNotificationResponse>> getReadNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getReadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }


    /**
     * Đánh dấu tất cả notifications là đã đọc
     * PUT /api/v1/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Integer userId) {
        notificationService.markAllAsRead(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa một notification của user
     * DELETE /api/v1/notifications/user/{userId}/notification/{notificationId}
     */
    @DeleteMapping("/user/{userId}/notification/{notificationId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> deleteUserNotification(
            @PathVariable Integer userId,
            @PathVariable Integer notificationId) {
        notificationService.deleteUserNotification(userId, notificationId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Gửi notification đến người dùng đã đăng ký sự kiện
     * POST /api/v1/notifications/send
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ORGANIZATION')")
    public ResponseEntity<Map<String, Object>> sendNotificationToEventParticipants(
            @RequestBody io.volunteerapp.volunteer_app.DTO.requeset.SendNotificationRequest request) {
        int sentCount = notificationService.sendNotificationToEventParticipants(request);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notification sent successfully");
        response.put("sentCount", sentCount);
        return ResponseEntity.ok(response);
    }
}

