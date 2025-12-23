package com.manhhuy.myapplication.helper.request;

public class SendNotificationRequest {
    private Integer eventId;
    private String title;
    private String content;
    private String attachmentUrl;
    private String recipientType; // ALL, APPROVED, PENDING
    
    public SendNotificationRequest() {}
    
    public SendNotificationRequest(Integer eventId, String title, String content, String recipientType, String attachmentUrl) {
        this.eventId = eventId;
        this.title = title;
        this.content = content;
        this.recipientType = recipientType;
        this.attachmentUrl = attachmentUrl;
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

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
    
    public String getRecipientType() {
        return recipientType;
    }
    
    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }
}
