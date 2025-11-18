package com.manhhuy.myapplication.helper.request;

public class EventRequest {
    private String title;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private Integer maxParticipants;
    private String status;
    private Integer eventTypeId;
    private Boolean hasCertificate;
    private Boolean hasReward;
    private Integer rewardId;
    
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
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public Integer getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getEventTypeId() {
        return eventTypeId;
    }
    
    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    
    public Boolean getHasCertificate() {
        return hasCertificate;
    }
    
    public void setHasCertificate(Boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }
    
    public Boolean getHasReward() {
        return hasReward;
    }
    
    public void setHasReward(Boolean hasReward) {
        this.hasReward = hasReward;
    }
    
    public Integer getRewardId() {
        return rewardId;
    }
    
    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }
}
