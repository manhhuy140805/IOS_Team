package com.manhhuy.myapplication.helper.request;

public class EventRegistrationRequest {
    private Integer eventId;
    private String notes;
    
    public EventRegistrationRequest() {}
    
    public EventRegistrationRequest(Integer eventId, String notes) {
        this.eventId = eventId;
        this.notes = notes;
    }
    
    public Integer getEventId() {
        return eventId;
    }
    
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
