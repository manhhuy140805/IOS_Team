package com.manhhuy.myapplication.model;

public class RewardItem {
    private String name;
    private String organization;
    private String description;
    private String points;
    private String stock;
    private String expiry;
    private int categoryType; // 0=all, 1=voucher, 2=gift, 3=opportunity
    private String tag1;
    private String tag2;
    private int iconColorIndex; // 0=purple, 1=pink, 2=orange, 3=cyan
    private String imageUrl; // URL hình ảnh từ API

    public RewardItem(String name, String organization, String description, String points,
            String stock, String expiry, int categoryType, String tag1, String tag2, int iconColorIndex) {
        this.name = name;
        this.organization = organization;
        this.description = description;
        this.points = points;
        this.stock = stock;
        this.expiry = expiry;
        this.categoryType = categoryType;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.iconColorIndex = iconColorIndex;
    }

    public RewardItem(String name, String organization, String description, String points,
            String stock, String expiry, int categoryType, String tag1, String tag2, 
            int iconColorIndex, String imageUrl) {
        this.name = name;
        this.organization = organization;
        this.description = description;
        this.points = points;
        this.stock = stock;
        this.expiry = expiry;
        this.categoryType = categoryType;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.iconColorIndex = iconColorIndex;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public int getIconColorIndex() {
        return iconColorIndex;
    }

    public void setIconColorIndex(int iconColorIndex) {
        this.iconColorIndex = iconColorIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
