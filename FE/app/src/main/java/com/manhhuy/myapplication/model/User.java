package com.manhhuy.myapplication.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String joinDate;
    private int activityCount;
    private String lastActive;
    private String status; // "Hoạt động", "Bị khóa", "Chờ xác thực"
    private String violationType; // "Spam", null if no violation

    public User() {
    }

    public User(String id, String name, String email, String joinDate, int activityCount, String lastActive, String status, String violationType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
        this.activityCount = activityCount;
        this.lastActive = lastActive;
        this.status = status;
        this.violationType = violationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
}
