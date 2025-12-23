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
    private final NotificationService notificationService;

    public EventRegistrationService(EventRegistrationRepository registrationRepository,
            EventRepository eventRepository,
            UserRepository userRepository,
            EventRegistrationMapper registrationMapper,
            NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationMapper = registrationMapper;
        this.notificationService = notificationService;
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
        if (request.getNotes() != null) {
            registration.setNotes(request.getNotes());
        }

        EventRegistration saved = registrationRepository.save(registration);
        return registrationMapper.toResponse(saved);
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
                .map(registrationMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                registrationPage.getNumber(),
                registrationPage.getTotalElements(),
                registrationPage.getTotalPages());
    }

    // Get registrations for all events created by current organization
    public PageResponse<EventRegistrationResponse> getMyEventsRegistrations(int page, int size, String status) {
        User currentUser = getCurrentUser();
        System.out.println("Current user: " + currentUser.getEmail() + " (ID: " + currentUser.getId() + ")");

        // Get all events created by this user (organization)
        List<Event> myEvents = eventRepository.findByCreator(currentUser);
        System.out.println("Found " + myEvents.size() + " events created by user");

        if (myEvents.isEmpty()) {
            return new PageResponse<>(
                    java.util.Collections.emptyList(),
                    page,
                    0L,
                    0);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("joinDate").descending());
        Page<EventRegistration> registrationPage;

        if (status != null && !status.isEmpty()) {
            registrationPage = registrationRepository.findByEventInAndStatus(myEvents, status, pageable);
            System.out.println("Filtered by status: " + status + ", found " + registrationPage.getTotalElements() + " registrations");
        } else {
            registrationPage = registrationRepository.findByEventIn(myEvents, pageable);
            System.out.println("No status filter, found " + registrationPage.getTotalElements() + " registrations");
        }

        List<EventRegistrationResponse> responses = registrationPage.getContent().stream()
                .map(registrationMapper::toResponse)
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
                .map(registrationMapper::toResponse)
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
        
        // Send notification to user about registration status
        sendRegistrationStatusNotification(updated, status);
        
        return registrationMapper.toResponse(updated);
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
        return registrationMapper.toResponse(updated);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private void sendRegistrationStatusNotification(EventRegistration registration, String status) {
        try {
            String title;
            String content;
            
            if ("APPROVED".equals(status)) {
                title = "Đăng ký sự kiện được chấp nhận";
                content = "Chúc mừng! Đăng ký tham gia sự kiện \"" + registration.getEvent().getTitle() + 
                         "\" của bạn đã được chấp nhận. Vui lòng chuẩn bị tham gia sự kiện.";
            } else if ("REJECTED".equals(status)) {
                title = "Đăng ký sự kiện bị từ chối";
                content = "Rất tiếc, đăng ký tham gia sự kiện \"" + registration.getEvent().getTitle() + 
                         "\" của bạn đã bị từ chối. Vui lòng thử lại với sự kiện khác.";
            } else {
                return; // Don't send notification for other statuses
            }
            
            notificationService.sendSystemNotification(
                registration.getUser().getId(),
                title,
                content
            );
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Failed to send registration notification: " + e.getMessage());
        }
    }
}
