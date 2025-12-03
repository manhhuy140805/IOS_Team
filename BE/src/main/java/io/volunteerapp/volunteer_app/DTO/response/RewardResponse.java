package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    private Integer id;
    private String name;
    private String description;
    private String type;
    private String imageUrl;
    private Integer pointsRequired;
    private Integer quantity;
    private String status;
    private LocalDate expiryDate;
    private Instant createdAt;
    private Instant updatedAt;

    // Provider info
    private Integer providerId;
    private String providerName;
}
