package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Integer id;
    private String title;
    private String description;
    private String location;
    private String imageUrl;
    private Date eventStartTime;
    private Date eventEndTime;
    private Integer numOfVolunteers;
    private Integer rewardPoints;
    private String status;
    private String category;
    private Instant createdAt;
    private Instant updatedAt;

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
}
