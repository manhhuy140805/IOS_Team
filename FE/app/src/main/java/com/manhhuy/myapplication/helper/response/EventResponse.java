package com.manhhuy.myapplication.helper.response;

import android.os.Parcel;
import android.os.Parcelable;

public class EventResponse implements Parcelable {
    private Integer id;
    private String title;
    private String description;
    private String location;
    private String imageUrl;
    private String eventStartTime;
    private String eventEndTime;
    private Integer numOfVolunteers;
    private Integer rewardPoints;
    private String status;
    private String category;
    private String createdAt;
    private String updatedAt;
    
    // Creator info
    private Integer creatorId;
    private String creatorName;
    private String creatorEmail;
    
    // Event type info
    private Integer eventTypeId;
    private String eventTypeName;
    
    // Registration stats
    private Integer currentParticipants;
    private Integer availableSlots;
    
    public EventResponse() {}
    
    protected EventResponse(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        description = in.readString();
        location = in.readString();
        imageUrl = in.readString();
        eventStartTime = in.readString();
        eventEndTime = in.readString();
        if (in.readByte() == 0) {
            numOfVolunteers = null;
        } else {
            numOfVolunteers = in.readInt();
        }
        if (in.readByte() == 0) {
            rewardPoints = null;
        } else {
            rewardPoints = in.readInt();
        }
        status = in.readString();
        category = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            creatorId = null;
        } else {
            creatorId = in.readInt();
        }
        creatorName = in.readString();
        creatorEmail = in.readString();
        if (in.readByte() == 0) {
            eventTypeId = null;
        } else {
            eventTypeId = in.readInt();
        }
        eventTypeName = in.readString();
        if (in.readByte() == 0) {
            currentParticipants = null;
        } else {
            currentParticipants = in.readInt();
        }
        if (in.readByte() == 0) {
            availableSlots = null;
        } else {
            availableSlots = in.readInt();
        }
    }
    
    public static final Creator<EventResponse> CREATOR = new Creator<EventResponse>() {
        @Override
        public EventResponse createFromParcel(Parcel in) {
            return new EventResponse(in);
        }

        @Override
        public EventResponse[] newArray(int size) {
            return new EventResponse[size];
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeString(imageUrl);
        dest.writeString(eventStartTime);
        dest.writeString(eventEndTime);
        if (numOfVolunteers == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numOfVolunteers);
        }
        if (rewardPoints == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(rewardPoints);
        }
        dest.writeString(status);
        dest.writeString(category);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        if (creatorId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(creatorId);
        }
        dest.writeString(creatorName);
        dest.writeString(creatorEmail);
        if (eventTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventTypeId);
        }
        dest.writeString(eventTypeName);
        if (currentParticipants == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(currentParticipants);
        }
        if (availableSlots == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(availableSlots);
        }
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getEventStartTime() {
        return eventStartTime;
    }
    
    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }
    
    public String getEventEndTime() {
        return eventEndTime;
    }
    
    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }
    
    public Integer getNumOfVolunteers() {
        return numOfVolunteers;
    }
    
    public void setNumOfVolunteers(Integer numOfVolunteers) {
        this.numOfVolunteers = numOfVolunteers;
    }
    
    public Integer getRewardPoints() {
        return rewardPoints;
    }
    
    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
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
    
    public Integer getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public String getCreatorEmail() {
        return creatorEmail;
    }
    
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
    
    public Integer getEventTypeId() {
        return eventTypeId;
    }
    
    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    
    public String getEventTypeName() {
        return eventTypeName;
    }
    
    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }
    
    public Integer getCurrentParticipants() {
        return currentParticipants;
    }
    
    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
    
    public Integer getAvailableSlots() {
        return availableSlots;
    }
    
    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }
}
