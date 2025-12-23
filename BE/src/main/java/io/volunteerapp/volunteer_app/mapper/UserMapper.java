package io.volunteerapp.volunteer_app.mapper;

import org.mapstruct.*;

import io.volunteerapp.volunteer_app.DTO.requeset.UpdateUserRequest;
import io.volunteerapp.volunteer_app.DTO.response.UserResponse;
import io.volunteerapp.volunteer_app.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(UserResponse userResponse);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "totalPoints", ignore = true)
    @Mapping(target = "violation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "creatorEvents", ignore = true)
    @Mapping(target = "userEventRegistrations", ignore = true)
    @Mapping(target = "userUserRewards", ignore = true)
    @Mapping(target = "userNotifications", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest updateUserRequest, @MappingTarget User user);

}
