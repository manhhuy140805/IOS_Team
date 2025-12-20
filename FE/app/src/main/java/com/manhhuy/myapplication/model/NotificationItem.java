package com.manhhuy.myapplication.model;

import java.util.Date;

public class NotificationItem {
    private int id;
    private String title;
    private String content;
    private String senderRole; // ADMIN, ORGANIZATION, SYSTEM
    private String type; // PERSONAL, ORGANIZATION, GLOBAL
    private boolean isRead;
    private Date createdAt;
    private Date readAt;
    private String attached; // Optional attachment URL

    public NotificationItem() {
    }

    public NotificationItem(int id, String title, String content, String senderRole, 
                          String type, boolean isRead, Date createdAt, Date readAt, String attached) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.senderRole = senderRole;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.attached = attached;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public String getAttached() {
        return attached;
    }

    public void setAttached(String attached) {
        this.attached = attached;
    }
}
