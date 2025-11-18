package com.manhhuy.myapplication.helper.response;

public class EventResponse {
    private Integer id;
    private String title;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status;
    private Integer eventTypeId;
    private String eventTypeName;
    private Boolean hasCertificate;
    private Boolean hasReward;
    private Integer rewardId;
    private String rewardName;
    private Integer createdBy;
    private String creatorName;
    private String createdAt;
    private String updatedAt;
    
    public EventResponse() {}
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
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
    
    public Integer getCurrentParticipants() {
        return currentParticipants;
    }
    
    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
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
    
    public String getEventTypeName() {
        return eventTypeName;
    }
    
    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
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
    
    public String getRewardName() {
        return rewardName;
    }
    
    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }
    
    public Integer getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
