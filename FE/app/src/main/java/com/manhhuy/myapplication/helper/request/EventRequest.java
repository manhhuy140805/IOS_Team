package com.manhhuy.myapplication.helper.request;

public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String imageUrl;
    private String eventStartTime;
    private String eventEndTime;
    private Integer numOfVolunteers;
    private Integer rewardPoints;
    private String status;
    private String category;
    private Integer eventTypeId;
    
    public EventRequest() {}
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getEventStartTime() {
        return eventStartTime;
    }
    
    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }
    
    public String getEventEndTime() {
        return eventEndTime;
    }
    
    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
    
    public Integer getNumOfVolunteers() {
        return numOfVolunteers;
    }
    
    public void setNumOfVolunteers(Integer numOfVolunteers) {
        this.numOfVolunteers = numOfVolunteers;
    }
    
    public Integer getRewardPoints() {
        return rewardPoints;
    }
    
    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getEventTypeId() {
        return eventTypeId;
    }
    
    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
}
