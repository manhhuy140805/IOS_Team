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
    private Date eventStartTime;
    private Date eventEndTime;
    private Date registrationOpenTime;
    private Date registrationCloseTime;
    private Integer numOfVolunteers;
    private Integer rewardPoints;
    private String status;
    private Boolean hasCertificate;
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
    private Integer totalRegistrations;
    private Integer availableSlots;
}
