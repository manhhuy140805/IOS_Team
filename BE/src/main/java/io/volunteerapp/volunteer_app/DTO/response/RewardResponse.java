package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer pointsRequired;
    private Integer quantity;
    private Instant createdAt;
    private Instant updatedAt;
}
