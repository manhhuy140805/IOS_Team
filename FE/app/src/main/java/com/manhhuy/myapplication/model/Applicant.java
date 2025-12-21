package com.manhhuy.myapplication.model;

public class Applicant {
    private String name;
    private String email;
    private String activityName;
    private String registrationDate;
    private String phone;
    private String note;
    private int status; // 0=pending, 1=accepted, 2=rejected
    private String avatarUrl;
    private String registrationId;

    public Applicant(String name, String email, String activityName, String registrationDate,
            String phone, String note, int status, String avatarUrl, String registrationId) {
        this.name = name;
        this.email = email;
        this.activityName = activityName;
        this.registrationDate = registrationDate;
        this.phone = phone;
        this.note = note;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.registrationId = registrationId;
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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
