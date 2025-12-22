package com.manhhuy.myapplication.helper.request;

public class SendNotificationRequest {
    private Integer eventId;
    private String title;
    private String content;
    private String recipientType; // ALL, APPROVED, PENDING
    
    public SendNotificationRequest() {}
    
    public SendNotificationRequest(Integer eventId, String title, String content, String recipientType) {
        this.eventId = eventId;
        this.title = title;
        this.content = content;
        this.recipientType = recipientType;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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
    
    public String getRecipientType() {
        return recipientType;
    }
    
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }
}
