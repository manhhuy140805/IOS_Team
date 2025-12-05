package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.EventRegistrationRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventRegistrationResponse;
import io.volunteerapp.volunteer_app.mapper.EventRegistrationMapper;
import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.EventRegistration;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.repository.EventRegistrationRepository;
import io.volunteerapp.volunteer_app.repository.EventRepository;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRegistrationMapper registrationMapper;

    public EventRegistrationService(EventRegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            EventRegistrationMapper registrationMapper) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationMapper = registrationMapper;
    }

    // Register for an event
    @Transactional
    public EventRegistrationResponse registerForEvent(EventRegistrationRequest request) {
        User currentUser = getCurrentUser();
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + request.getEventId()));

        // Check if event is full
        long currentRegistrations = registrationRepository.countByEvent(event);
        if (currentRegistrations >= event.getNumOfVolunteers()) {
            throw new RuntimeException("Event is full");
        }

        // Check if user already registered
        boolean alreadyRegistered = registrationRepository.existsByUserAndEvent(currentUser, event);
        if (alreadyRegistered) {
            throw new RuntimeException("You have already registered for this event");
        }

        EventRegistration registration = new EventRegistration();
        registration.setUser(currentUser);
        registration.setEvent(event);
        registration.setStatus("PENDING");
        registration.setCheckedIn(false);
        registration.setJoinDate(Date.valueOf(LocalDate.now()));

        EventRegistration saved = registrationRepository.save(registration);
        return convertToResponse(saved);
    }

    // Get registrations for an event (Admin only)
    public PageResponse<EventRegistrationResponse> getEventRegistrations(
            Integer eventId, int page, int size, String status) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("joinDate").descending());
        Page<EventRegistration> registrationPage;

        if (status != null && !status.isEmpty()) {
            registrationPage = registrationRepository.findByEventAndStatus(event, status, pageable);
        } else {
            registrationPage = registrationRepository.findByEvent(event, pageable);
        }

        List<EventRegistrationResponse> responses = registrationPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                registrationPage.getNumber(),
                registrationPage.getTotalElements(),
                registrationPage.getTotalPages());
    }

    // Get current user's registrations
    public PageResponse<EventRegistrationResponse> getMyRegistrations(int page, int size, String status) {
        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by("joinDate").descending());
        Page<EventRegistration> registrationPage;

        if (status != null && !status.isEmpty()) {
            registrationPage = registrationRepository.findByUserAndStatus(currentUser, status, pageable);
        } else {
            registrationPage = registrationRepository.findByUser(currentUser, pageable);
        }

        List<EventRegistrationResponse> responses = registrationPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PageResponse<>(
                responses,
                registrationPage.getNumber(),
                registrationPage.getTotalElements(),
                registrationPage.getTotalPages());
    }

    // Cancel registration
    @Transactional
    public void cancelRegistration(Integer registrationId) {
        User currentUser = getCurrentUser();
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));

        // Check if user owns this registration
        if (!registration.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only cancel your own registrations");
        }

        registrationRepository.delete(registration);
    }

    // Update registration status (Admin only)
    @Transactional
    public EventRegistrationResponse updateRegistrationStatus(Integer registrationId, String status) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));

        registration.setStatus(status);
        EventRegistration updated = registrationRepository.save(registration);
        return convertToResponse(updated);
    }

    // Check-in user (Admin only)
    @Transactional
    public EventRegistrationResponse checkInUser(Integer registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));

        registration.setCheckedIn(true);
        registration.setCheckedInAt(Instant.now());
        registration.setStatus("COMPLETED");
        EventRegistration updated = registrationRepository.save(registration);
        return convertToResponse(updated);
    }

    private EventRegistrationResponse convertToResponse(EventRegistration registration) {
        EventRegistrationResponse response = new EventRegistrationResponse();
        response.setId(registration.getId());
        response.setEventId(registration.getEvent().getId());
        response.setEventTitle(registration.getEvent().getTitle());
        response.setUserId(registration.getUser().getId());
        response.setUserName(registration.getUser().getFullName());
        response.setUserEmail(registration.getUser().getEmail());
        response.setUserPhone(registration.getUser().getPhone());
        response.setUserAvatarUrl(registration.getUser().getAvatarUrl());
        response.setStatus(registration.getStatus());
        response.setNotes(registration.getNotes());
        response.setCheckedIn(registration.getCheckedIn());
        response.setCheckedInAt(registration.getCheckedInAt());
        response.setJoinDate(registration.getJoinDate());
        response.setNotificationContent(registration.getNotificationContent());
        return response;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
