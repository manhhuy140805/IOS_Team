package com.manhhuy.myapplication.model;

public class SearchResult {
    private String title;
    private String organization;
    private String location;
    private int imageResId;
    private String imageUrl;
    private String category;
    private String subcategory;
    private String description;
    private String deadline;
    private int registeredCount;
    private int totalSlots;
    private String duration;

    // Constructor with drawable ID
    public SearchResult(String title, String organization, String location, int imageResId, 
                       String category, String subcategory, String description, String deadline,
                       int registeredCount, int totalSlots, String duration) {
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.imageResId = imageResId;
        this.imageUrl = null;
        this.category = category;
        this.subcategory = subcategory;
        this.description = description;
        this.deadline = deadline;
        this.registeredCount = registeredCount;
        this.totalSlots = totalSlots;
        this.duration = duration;
    }

    // Constructor with image URL
    public SearchResult(String title, String organization, String location, String imageUrl, 
                       String category, String subcategory, String description, String deadline,
                       int registeredCount, int totalSlots, String duration) {
        this.title = title;
        this.organization = organization;
        this.location = location;
        this.imageResId = 0;
        this.imageUrl = imageUrl;
        this.category = category;
        this.subcategory = subcategory;
        this.description = description;
        this.deadline = deadline;
        this.registeredCount = registeredCount;
        this.totalSlots = totalSlots;
        this.duration = duration;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

