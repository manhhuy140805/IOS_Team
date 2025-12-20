package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.response.RewardTypeResponse;
import io.volunteerapp.volunteer_app.model.RewardType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RewardTypeMapper {

    RewardTypeResponse toResponse(RewardType rewardType);
}
