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
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

        private final EventRepository eventRepository;
        private final EventTypeRepository eventTypeRepository;
        private final UserRepository userRepository;
        private final EventMapper eventMapper;
        private final GeminiService geminiService;

        public EventService(EventRepository eventRepository, EventTypeRepository eventTypeRepository,
                        UserRepository userRepository, EventMapper eventMapper, GeminiService geminiService) {
                this.eventRepository = eventRepository;
                this.eventTypeRepository = eventTypeRepository;
                this.userRepository = userRepository;
                this.eventMapper = eventMapper;
                this.geminiService = geminiService;
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

                // Build base specification
                Specification<Event> specification = EventSepcification.hasTitle(title)
                                .and(EventSepcification.hasLocation(location))
                                .and(EventSepcification.hasEventType(eventTypeId))
                                .and(EventSepcification.eventStartTimeAfter(startDateFrom))
                                .and(EventSepcification.eventStartTimeBefore(startDateTo))
                                .and(EventSepcification.hasRewardPoints(hasReward));

                // If status is provided (e.g., from Admin), use it
                // Otherwise, default to ACTIVE status for regular users
                if (status != null && !status.isEmpty()) {
                        specification = specification.and(EventSepcification.hasStatus(status));
                } else {
                        // For regular users: only ACTIVE events (show all including expired)
                        specification = specification.and(EventSepcification.hasStatus("ACTIVE"));
                }

                Page<Event> eventPage = eventRepository.findAll(specification, pageable);

                // Convert to response and set isExpired flag
                Date today = new Date(System.currentTimeMillis());
                List<EventResponse> responses = eventPage.getContent().stream()
                                .map(event -> {
                                        EventResponse response = convertToResponse(event);
                                        // Set isExpired based on eventEndTime
                                        boolean expired = event.getEventEndTime() != null
                                                        && event.getEventEndTime().before(today);
                                        response.setIsExpired(expired);
                                        return response;
                                })
                                .toList();

                // Sort: non-expired first, then expired at bottom
                List<EventResponse> sortedResponses = new java.util.ArrayList<>(responses);
                sortedResponses.sort((a, b) -> {
                        boolean aExpired = Boolean.TRUE.equals(a.getIsExpired());
                        boolean bExpired = Boolean.TRUE.equals(b.getIsExpired());
                        if (aExpired == bExpired)
                                return 0;
                        return aExpired ? 1 : -1; // Expired goes to bottom
                });

                return new PageResponse<>(
                                sortedResponses,
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

        // Get events by creator email (lấy từ JWT token)
        public PageResponse<EventResponse> getEventsByCreatorEmail(String email, int page, int size, String sortBy,
                        String sortDirection) {
                User creator = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

                Sort sort = sortDirection.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);

                Specification<Event> specification = EventSepcification.hasCreator(creator.getId());
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
                                .orElseThrow(() -> new RuntimeException(
                                                "Event type not found with id: " + eventTypeId));

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
                                                () -> new RuntimeException("Event type not found with id: "
                                                                + eventRequest.getEventTypeId()));

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
                                                        "Event type not found with id: "
                                                                        + eventRequest.getEventTypeId()));
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

        // Update event status (Admin only)
        @Transactional
        public EventResponse updateEventStatus(Integer id, String status) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

                event.setStatus(status);
                Event updatedEvent = eventRepository.save(event);
                return convertToResponse(updatedEvent);
        }

        // Search events using AI (Gemini) - single query, AI will extract info
        public io.volunteerapp.volunteer_app.DTO.response.AiSearchResponse searchEventsByAI(String userQuery) {
                // 1. Get all events from database
                List<Event> allEvents = eventRepository.findAll();

                if (allEvents.isEmpty()) {
                        PageResponse<EventResponse> emptyPage = new PageResponse<>(
                                        new ArrayList<>(),
                                        0,
                                        0L,
                                        0);
                        return new io.volunteerapp.volunteer_app.DTO.response.AiSearchResponse(
                                        "Hiện tại chưa có sự kiện nào trong hệ thống. Hãy quay lại sau nhé! 💚",
                                        false,
                                        emptyPage);
                }

                // 2. Use Gemini to analyze events and find matches
                GeminiService.AiAnalysisResult aiResult = geminiService.analyzeEventsForSearch(allEvents, userQuery);

                if (aiResult.eventIds.isEmpty()) {
                        PageResponse<EventResponse> emptyPage = new PageResponse<>(
                                        new ArrayList<>(),
                                        0,
                                        0L,
                                        0);
                        return new io.volunteerapp.volunteer_app.DTO.response.AiSearchResponse(
                                        aiResult.explanation,
                                        aiResult.foundMatch,
                                        emptyPage);
                }

                // 3. Fetch matching events from database
                List<Event> matchingEvents = eventRepository.findAllById(aiResult.eventIds);

                // 4. Convert to response
                List<EventResponse> responses = matchingEvents.stream()
                                .map(this::convertToResponse)
                                .toList();

                PageResponse<EventResponse> pageResponse = new PageResponse<>(
                                responses,
                                0,
                                (long) responses.size(),
                                1);

                return new io.volunteerapp.volunteer_app.DTO.response.AiSearchResponse(
                                aiResult.explanation,
                                aiResult.foundMatch,
                                pageResponse);
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
