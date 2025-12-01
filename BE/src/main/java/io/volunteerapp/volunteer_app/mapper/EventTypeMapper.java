package io.volunteerapp.volunteer_app.mapper;

import io.volunteerapp.volunteer_app.DTO.requeset.EventTypeRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventTypeResponse;
import io.volunteerapp.volunteer_app.model.EventType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTypeEvents", ignore = true)
    EventType toEntity(EventTypeRequest eventTypeRequest);

    EventTypeResponse toResponse(EventType eventType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventTypeEvents", ignore = true)
    void updateEntityFromRequest(EventTypeRequest eventTypeRequest, @MappingTarget EventType eventType);
}
