package com.manhhuy.myapplication.helper.request;

public class ClaimRewardRequest {
    private Integer rewardId;
    private String notes;

    public ClaimRewardRequest() {
    }

    public ClaimRewardRequest(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public ClaimRewardRequest(Integer rewardId, String notes) {
        this.rewardId = rewardId;
        this.notes = notes;
    }

    public Integer getRewardId() {
        return rewardId;
    }

    public void setRewardId(Integer rewardId) {
        this.rewardId = rewardId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
