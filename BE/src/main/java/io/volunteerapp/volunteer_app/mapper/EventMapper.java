package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.requeset.EventRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventResponse;
import io.volunteerapp.volunteer_app.model.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "eventEventRegistrations", ignore = true)
    Event toEntity(EventRequest eventRequest);

    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.fullName", target = "creatorName")
    @Mapping(source = "creator.email", target = "creatorEmail")
    @Mapping(source = "eventType.id", target = "eventTypeId")
    @Mapping(source = "eventType.name", target = "eventTypeName")
    @Mapping(target = "totalRegistrations", ignore = true)
    @Mapping(target = "availableSlots", ignore = true)
    EventResponse toResponse(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "eventEventRegistrations", ignore = true)
    void updateEntityFromRequest(EventRequest eventRequest, @MappingTarget Event event);
}
