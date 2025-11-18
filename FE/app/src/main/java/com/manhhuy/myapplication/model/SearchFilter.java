package com.manhhuy.myapplication.model;

import java.util.Date;

public class SearchFilter {
    private String keyword;
    private String category;
    private Date startDate;
    private Date endDate;
    private String sortBy;

    public SearchFilter() {
    }

    public SearchFilter(String keyword, String category, Date startDate, Date endDate, String sortBy) {
        this.keyword = keyword;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sortBy = sortBy;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
