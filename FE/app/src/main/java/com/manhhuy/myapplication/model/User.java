package com.manhhuy.myapplication.model;

import java.util.Date;

// User model for volunteer management system
public class User {
    private Integer id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String avatarUrl;
    private String role; // VOLUNTEER, ORGANIZER, ADMIN
    private String status; // ACTIVE, LOCKED, PENDING
    private Date createdAt;
    private Date updatedAt;

    // Additional fields for UI
    private int eventsCount;
    private int pointsCount;

    // Additional fields for User Management
    private String joinDate;
    private int activityCount;
    private String lastActive;
    private String violationType;

    public User() {
    }

    public User(Integer id, String fullName, String email, String phone, String avatarUrl,
                String role, String status, Date createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = new Date();
    }

    // Constructor for User Management
    public User(String id, String fullName, String email, String joinDate,
                int activityCount, String lastActive, String status, String violationType) {
        this.id = id != null ? Integer.parseInt(id) : null;
        this.fullName = fullName;
        this.email = email;
        this.joinDate = joinDate;
        this.activityCount = activityCount;
        this.lastActive = lastActive;
        this.status = status;
        this.violationType = violationType;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(int eventsCount) {
        this.eventsCount = eventsCount;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    // User Management getters and setters
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
}
