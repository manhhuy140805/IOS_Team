package com.manhhuy.myapplication.model;

public class Category {
    private String name;
    private int jobCount;
    private int iconResId;

    public Category(String name, int jobCount, int iconResId) {
        this.name = name;
        this.jobCount = jobCount;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJobCount() {
        return jobCount;
    }

    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
