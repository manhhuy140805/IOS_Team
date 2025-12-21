package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.EventRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventResponse;
import io.volunteerapp.volunteer_app.mapper.EventMapper;
import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.EventType;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.EventRepository;
import io.volunteerapp.volunteer_app.repository.EventTypeRepository;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import io.volunteerapp.volunteer_app.service.Sepcification.EventSepcification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, EventTypeRepository eventTypeRepository,
            UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    // Get all events with filters and pagination
    public PageResponse<EventResponse> getAllEvents(
            int page, int size, String sortBy, String sortDirection,
            String title, String location, String status, Integer eventTypeId,
            Date startDateFrom, Date startDateTo, Boolean hasReward) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Event> specification = EventSepcification.hasTitle(title)
                .and(EventSepcification.hasLocation(location))
                .and(EventSepcification.hasStatus(status))
                .and(EventSepcification.hasEventType(eventTypeId))
                .and(EventSepcification.eventStartTimeAfter(startDateFrom))
                .and(EventSepcification.eventStartTimeBefore(startDateTo))
                .and(EventSepcification.hasRewardPoints(hasReward));

        Page<Event> eventPage = eventRepository.findAll(specification, pageable);
        List<EventResponse> responses = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                eventPage.getNumber(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages());
    }

    // Get event by ID
    public EventResponse getEventById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return convertToResponse(event);
    }

    // Get events created by current user
    public PageResponse<EventResponse> getMyEvents(int page, int size, String sortBy, String sortDirection) {
        User currentUser = getCurrentUser();

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Event> specification = EventSepcification.hasCreator(currentUser.getId());
        Page<Event> eventPage = eventRepository.findAll(specification, pageable);

        List<EventResponse> responses = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                eventPage.getNumber(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages());
    }

    // Get events by event type
    public PageResponse<EventResponse> getEventsByType(
            Integer eventTypeId, int page, int size, String sortBy, String sortDirection) {
        
        // Verify event type exists
        eventTypeRepository.findById(eventTypeId)
                .orElseThrow(() -> new RuntimeException("Event type not found with id: " + eventTypeId));

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Event> specification = EventSepcification.hasEventType(eventTypeId);
        Page<Event> eventPage = eventRepository.findAll(specification, pageable);

        List<EventResponse> responses = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                eventPage.getNumber(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages());
    }

    // Search events by query text (searches in title)
    public PageResponse<EventResponse> searchEvents(
            String query, int page, int size, String sortBy, String sortDirection) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Search only in title field
        Specification<Event> specification = EventSepcification.hasTitle(query);
        Page<Event> eventPage = eventRepository.findAll(specification, pageable);

        List<EventResponse> responses = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                eventPage.getNumber(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages());
    }

    // Get events by user ID (for admin)
    public PageResponse<EventResponse> getEventsByUserId(Integer userId, int page, int size) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<Event> specification = EventSepcification.hasCreator(userId);
        Page<Event> eventPage = eventRepository.findAll(specification, pageable);

        List<EventResponse> responses = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                eventPage.getNumber(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages());
    }

    // Create new event (Admin only)
    @Transactional
    public EventResponse createEvent(EventRequest eventRequest) {
        User currentUser = getCurrentUser();

        EventType eventType = eventTypeRepository.findById(eventRequest.getEventTypeId())
                .orElseThrow(
                        () -> new RuntimeException("Event type not found with id: " + eventRequest.getEventTypeId()));

        Event event = eventMapper.toEntity(eventRequest);
        event.setCreator(currentUser);
        event.setEventType(eventType);

        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus("PENDING");
        }

        Event savedEvent = eventRepository.save(event);
        return convertToResponse(savedEvent);
    }

    // Update event (Admin only)
    @Transactional
    public EventResponse updateEvent(Integer id, EventRequest eventRequest) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        if (eventRequest.getEventTypeId() != null &&
                !event.getEventType().getId().equals(eventRequest.getEventTypeId())) {
            EventType eventType = eventTypeRepository.findById(eventRequest.getEventTypeId())
                    .orElseThrow(() -> new RuntimeException(
                            "Event type not found with id: " + eventRequest.getEventTypeId()));
            event.setEventType(eventType);
        }

        eventMapper.updateEntityFromRequest(eventRequest, event);
        Event updatedEvent = eventRepository.save(event);
        return convertToResponse(updatedEvent);
    }

    // Delete event (Admin only)
    @Transactional
    public void deleteEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        eventRepository.delete(event);
    }

    // Helper method to convert Event to EventResponse
    private EventResponse convertToResponse(Event event) {
        EventResponse response = eventMapper.toResponse(event);

        // Calculate registration stats
        long approvedCount = event.getEventEventRegistrations().stream()
                .filter(reg -> "APPROVED".equals(reg.getStatus()))
                .count();
        int currentParticipants = (int) approvedCount;
        int availableSlots = event.getNumOfVolunteers() - currentParticipants;

        response.setCurrentParticipants(currentParticipants);
        response.setAvailableSlots(Math.max(0, availableSlots));

        return response;
    }

    // Get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println("DEBUG: Getting user with email: " + email);
        System.out.println("DEBUG: Authentication: " + authentication);
        System.out.println("DEBUG: Principal: " + authentication.getPrincipal());
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
