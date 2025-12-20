package com.manhhuy.myapplication.helper.response;

public class RewardTypeResponse {
    private Integer id;
    private String title;

    public RewardTypeResponse() {
    }

    public RewardTypeResponse(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

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

    @Override
    public String toString() {
        return "RewardTypeResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
