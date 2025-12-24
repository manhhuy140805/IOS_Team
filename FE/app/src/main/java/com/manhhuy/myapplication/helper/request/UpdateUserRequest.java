package com.manhhuy.myapplication.helper.request;

public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String address;
    private String avatarUrl;
    private String dateOfBirth;  // Format: yyyy-MM-dd
    private String gender;       // MALE, FEMALE, OTHER
    
    private String status;
    private String role;
    
    public UpdateUserRequest() {}
    
    public UpdateUserRequest(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }
    
    public UpdateUserRequest(String fullName, String phone, String address, String avatarUrl) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatarUrl = avatarUrl;
    }
    
    public UpdateUserRequest(String fullName, String phone, String address, String avatarUrl, 
                           String dateOfBirth, String gender) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatarUrl = avatarUrl;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
}
