package com.manhhuy.myapplication.helper.response;

public class EventRegistrationResponse {
    private Integer id;
    private Integer eventId;
    private String eventTitle;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAvatarUrl;
    private String status;
    private String notes;
    private String joinDate;
    private Boolean checkedIn;
    private String checkedInAt;
    private String notificationContent;
    
    public EventRegistrationResponse() {}
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    public String getEventTitle() {
        return eventTitle;
    }
    
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserPhone() {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
    
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }
    
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getJoinDate() {
        return joinDate;
    }
    
    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }
    
    public Boolean getCheckedIn() {
        return checkedIn;
    }
    
    public void setCheckedIn(Boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
    
    public String getCheckedInAt() {
        return checkedInAt;
    }
    
    public void setCheckedInAt(String checkedInAt) {
        this.checkedInAt = checkedInAt;
    }
    
    public String getNotificationContent() {
        return notificationContent;
    }
    
    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }
}
