package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

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
    private String status;
    private Boolean checkIn;
    private Date joinDate;
}
