package com.manhhuy.myapplication.helper.response;

public class UserNotificationResponse {
    private Integer id;
    private Integer userId;
    private NotificationResponse notification;
    private Boolean isRead;
    private String readAt;
    private String createdAt;

    public UserNotificationResponse() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public NotificationResponse getNotification() {
        return notification;
    }

    public void setNotification(NotificationResponse notification) {
        this.notification = notification;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getReadAt() {
        return readAt;
    }

    public void setReadAt(String readAt) {
        this.readAt = readAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
