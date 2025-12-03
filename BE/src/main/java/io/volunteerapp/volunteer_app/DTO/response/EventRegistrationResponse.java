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
public class EventRegistrationResponse {
    private Integer id;
    private Integer eventId;
    private String eventTitle;
    private Integer userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userAvatarUrl;
    private String status;
    private String notes;
    private Date joinDate;
    private Boolean checkedIn;
    private Instant checkedInAt;
    private String notificationContent;
}
