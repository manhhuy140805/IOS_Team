package com.manhhuy.myapplication.model;

public class UserManagement {
    private String id;
    private String name;
    private String email;
    private String joinDate;
    private int eventsCount;
    private String volunteerHours;
    private String status; // "Hoạt động", "Bị khóa", "Chờ xác thực"
    private String violationType; // "Spam", null if no violation

    public UserManagement() {
    }

    public UserManagement(String id, String name, String email, String joinDate,
                          int eventsCount, String volunteerHours, String status, String violationType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
        this.eventsCount = eventsCount;
        this.volunteerHours = volunteerHours;
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

    public int getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(int eventsCount) {
        this.eventsCount = eventsCount;
    }

    public String getVolunteerHours() {
        return volunteerHours;
    }

    public void setVolunteerHours(String volunteerHours) {
        this.volunteerHours = volunteerHours;
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

