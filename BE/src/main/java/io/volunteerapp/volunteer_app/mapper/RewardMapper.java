package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.requeset.RewardRequest;
import io.volunteerapp.volunteer_app.DTO.response.RewardResponse;
import io.volunteerapp.volunteer_app.model.Reward;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RewardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rewardUserRewards", ignore = true)
    Reward toEntity(RewardRequest rewardRequest);

    RewardResponse toResponse(Reward reward);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rewardUserRewards", ignore = true)
    void updateEntityFromRequest(RewardRequest rewardRequest, @MappingTarget Reward reward);
}
