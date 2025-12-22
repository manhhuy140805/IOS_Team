package com.manhhuy.myapplication.helper.response;

public class MyRewardResponse {
    private Integer id;
    private String status;
    private Integer pointsSpent;
    private String notes;
    private String createdAt;
    private String updatedAt;

    // User info
    private Integer userId;
    private String userName;
    private String userEmail;

    // Reward info
    private Integer rewardId;
    private String rewardName;
    private String rewardDescription;
    private Integer pointsRequired;
    private String rewardImageUrl;

    public MyRewardResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPointsSpent() {
        return pointsSpent;
    }

    public void setPointsSpent(Integer pointsSpent) {
        this.pointsSpent = pointsSpent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public Integer getPointsRequired() {
        return pointsRequired;
    }

    public void setPointsRequired(Integer pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public String getRewardImageUrl() {
        return rewardImageUrl;
    }

    public void setRewardImageUrl(String rewardImageUrl) {
        this.rewardImageUrl = rewardImageUrl;
    }

    /**
     * Get display status in Vietnamese
     */
    public String getDisplayStatus() {
        if (status == null)
            return "Không xác định";
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Đang xử lý";
            case "APPROVED":
                return "Đã duyệt";
            case "REJECTED":
                return "Bị từ chối";
            case "DELIVERED":
                return "Đã nhận";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }
}
