package io.volunteerapp.volunteer_app.DTO.requeset;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRewardRequest {

    @NotNull(message = "Reward ID is required")
    private Integer rewardId;
}
