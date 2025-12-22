package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.response.UserRewardResponse;
import io.volunteerapp.volunteer_app.model.UserReward;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRewardMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "reward.id", target = "rewardId")
    @Mapping(source = "reward.name", target = "rewardName")
    @Mapping(source = "reward.description", target = "rewardDescription")
    @Mapping(source = "reward.pointsRequired", target = "pointsRequired")
    @Mapping(source = "reward.imageUrl", target = "rewardImageUrl")
    UserRewardResponse toResponse(UserReward userReward);
}
