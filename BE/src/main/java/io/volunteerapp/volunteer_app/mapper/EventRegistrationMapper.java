package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.response.EventRegistrationResponse;
import io.volunteerapp.volunteer_app.model.EventRegistration;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRegistrationMapper {

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.phone", target = "userPhone")
    @Mapping(source = "user.avatarUrl", target = "userAvatarUrl")
    EventRegistrationResponse toResponse(EventRegistration eventRegistration);
}
