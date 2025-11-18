package com.manhhuy.myapplication.model;

public class Organization {
    private String id;
    private String name;
    private String email;
    private String foundedDate;
    private int memberCount;
    private String status; // "Hoạt động", "Bị khóa", "Chờ xác thực"
    private String violationType; // "Spam", null if no violation

    public Organization() {
    }

    public Organization(String id, String name, String email, String foundedDate, int memberCount, String status, String violationType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.foundedDate = foundedDate;
        this.memberCount = memberCount;
        this.status = status;
        this.violationType = violationType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoundedDate() {
        return foundedDate;
    }

    public void setFoundedDate(String foundedDate) {
        this.foundedDate = foundedDate;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
}
