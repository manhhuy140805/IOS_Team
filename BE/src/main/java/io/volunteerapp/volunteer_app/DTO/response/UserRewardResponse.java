package io.volunteerapp.volunteer_app.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRewardResponse {
    private Integer id;
    private String status;

    // User info
    private Integer userId;
    private String userName;
    private String userEmail;

    // Reward info
    private Integer rewardId;
    private String rewardName;
    private String rewardDescription;
    private Integer pointsRequired;
}
