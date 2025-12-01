package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.requeset.EventTypeRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventTypeResponse;
import io.volunteerapp.volunteer_app.mapper.EventTypeMapper;
import io.volunteerapp.volunteer_app.model.EventType;
import io.volunteerapp.volunteer_app.repository.EventTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;
    private final EventTypeMapper eventTypeMapper;

    public EventTypeService(EventTypeRepository eventTypeRepository, EventTypeMapper eventTypeMapper) {
        this.eventTypeRepository = eventTypeRepository;
        this.eventTypeMapper = eventTypeMapper;
    }

    // Get all event types
    public List<EventTypeResponse> getAllEventTypes() {
        return eventTypeRepository.findAll().stream()
                .map(eventTypeMapper::toResponse)
                .toList();
    }

    // Get event type by ID
    public EventTypeResponse getEventTypeById(Integer id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event type not found with id: " + id));
        return eventTypeMapper.toResponse(eventType);
    }

    // Create new event type (Admin only)
    @Transactional
    public EventTypeResponse createEventType(EventTypeRequest eventTypeRequest) {
        EventType eventType = eventTypeMapper.toEntity(eventTypeRequest);
        EventType savedEventType = eventTypeRepository.save(eventType);
        return eventTypeMapper.toResponse(savedEventType);
    }

    // Update event type (Admin only)
    @Transactional
    public EventTypeResponse updateEventType(Integer id, EventTypeRequest eventTypeRequest) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event type not found with id: " + id));

        eventTypeMapper.updateEntityFromRequest(eventTypeRequest, eventType);
        EventType updatedEventType = eventTypeRepository.save(eventType);
        return eventTypeMapper.toResponse(updatedEventType);
    }

    // Delete event type (Admin only)
    @Transactional
    public void deleteEventType(Integer id) {
        EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event type not found with id: " + id));
        eventTypeRepository.delete(eventType);
    }
}
