package io.volunteerapp.volunteer_app.DTO.requeset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    private String imageUrl;

    @NotNull(message = "Event start time is required")
    private Date eventStartTime;

    @NotNull(message = "Event end time is required")
    private Date eventEndTime;

    @NotNull(message = "Number of volunteers is required")
    @Min(value = 1, message = "Number of volunteers must be at least 1")
    private Integer numOfVolunteers;

    private Integer rewardPoints;

    private String status;

    private String category;

    @NotNull(message = "Event type ID is required")
    private Integer eventTypeId;
}
