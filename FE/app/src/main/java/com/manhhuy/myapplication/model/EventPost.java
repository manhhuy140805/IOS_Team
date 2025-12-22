package com.manhhuy.myapplication.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class EventPost implements Serializable {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String organizationName;
    private String organizationInitials;
    private String organizationColor;
    private List<String> tags;
    private String tagColor;
    private Date eventDate;
    private String location;
    private int rewardPoints;
    private String postedBy;
    private String postedTime;
    private String status; // "pending", "approved", "rejected"
    private String reviewedBy;
    private String reviewedTime;
    private String rejectionReason;
    private int currentParticipants;
    private int maxParticipants;

    // Constructor
    public EventPost() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getOrganizationInitials() { return organizationInitials; }
    public void setOrganizationInitials(String organizationInitials) { this.organizationInitials = organizationInitials; }

    public String getOrganizationColor() { return organizationColor; }
    public void setOrganizationColor(String organizationColor) { this.organizationColor = organizationColor; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getTagColor() { return tagColor; }
    public void setTagColor(String tagColor) { this.tagColor = tagColor; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getPostedTime() { return postedTime; }
    public void setPostedTime(String postedTime) { this.postedTime = postedTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedTime() { return reviewedTime; }
    public void setReviewedTime(String reviewedTime) { this.reviewedTime = reviewedTime; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
}
