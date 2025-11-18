package com.manhhuy.myapplication.model;

public class Event {
    private String title;
    private String location;
    private String organization;
    private int imageResId;
    private String compensation;
    private String organizerIcon;

    public Event(String title, String location, String organization, int imageResId, String compensation, String organizerIcon) {
        this.title = title;
        this.location = location;
        this.organization = organization;
        this.imageResId = imageResId;
        this.compensation = compensation;
        this.organizerIcon = organizerIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getCompensation() {
        return compensation;
    }

    public void setCompensation(String compensation) {
        this.compensation = compensation;
    }

    public String getOrganizerIcon() {
        return organizerIcon;
    }

    public void setOrganizerIcon(String organizerIcon) {
        this.organizerIcon = organizerIcon;
    }
}
