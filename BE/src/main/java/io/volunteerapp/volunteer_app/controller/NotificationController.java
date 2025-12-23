package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.response.NotificationResponse;
import io.volunteerapp.volunteer_app.DTO.response.UserNotificationResponse;
import io.volunteerapp.volunteer_app.Util.RestResponse;
import io.volunteerapp.volunteer_app.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<RestResponse<NotificationResponse>> getNotificationById(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        NotificationResponse response = notificationService.getNotificationById(id, userId);
        return ResponseEntity.ok(new RestResponse<>(200, "Success", response, null));
    }

    /**
     * Lấy tất cả notifications của user
     * GET /api/v1/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<List<UserNotificationResponse>>> getUserNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(new RestResponse<>(200, "Success", notifications, null));
    }

    /**
     * Lấy notifications chưa đọc của user
     * GET /api/v1/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<List<UserNotificationResponse>>> getUnreadNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(new RestResponse<>(200, "Success", notifications, null));
    }

    /**
     * Lấy notifications đã đọc của user
     * GET /api/v1/notifications/user/{userId}/read
     */
    @GetMapping("/user/{userId}/read")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<List<UserNotificationResponse>>> getReadNotifications(@PathVariable Integer userId) {
        List<UserNotificationResponse> notifications = notificationService.getReadNotifications(userId);
        return ResponseEntity.ok(new RestResponse<>(200, "Success", notifications, null));
    }


    /**
     * Đánh dấu tất cả notifications là đã đọc
     * PUT /api/v1/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<Void>> markAllAsRead(@PathVariable Integer userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(new RestResponse<>(200, "All notifications marked as read", null, null));
    }

    /**
     * Xóa một notification của user
     * DELETE /api/v1/notifications/user/{userId}/notification/{notificationId}
     */
    @DeleteMapping("/user/{userId}/notification/{notificationId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RestResponse<Void>> deleteUserNotification(
            @PathVariable Integer userId,
            @PathVariable Integer notificationId) {
        notificationService.deleteUserNotification(userId, notificationId);
        return ResponseEntity.ok(new RestResponse<>(200, "Notification deleted successfully", null, null));
    }

    /**
     * Upload attachment và trả về URL
     * POST /api/v1/notifications/upload-attachment
     */
    @PostMapping(value = "/upload-attachment", consumes = {"multipart/form-data"})
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ORGANIZATION')")
    public ResponseEntity<RestResponse<Map<String, String>>> uploadAttachment(
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("🔍 Upload request received");
            System.out.println("📁 File name: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("📊 File size: " + (file != null ? file.getSize() : 0) + " bytes");
            
            String url = notificationService.uploadAttachment(file);
            Map<String, String> data = new HashMap<>();
            data.put("url", url);
            data.put("message", "File uploaded successfully");
            
            RestResponse<Map<String, String>> response = new RestResponse<>();
            response.setStatus(200);
            response.setMessage("File uploaded successfully");
            response.setData(data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Upload error: " + e.getMessage());
            e.printStackTrace();
            
            RestResponse<Map<String, String>> response = new RestResponse<>();
            response.setStatus(400);
            response.setMessage("Upload failed");
            response.setError(e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Gửi notification đến người dùng đã đăng ký sự kiện
     * POST /api/v1/notifications/send
     */
    @PostMapping(value = "/send", consumes = {"application/json"})
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

