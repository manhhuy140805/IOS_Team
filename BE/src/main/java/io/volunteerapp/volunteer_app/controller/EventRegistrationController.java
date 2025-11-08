package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.EventRegistrationRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventRegistrationResponse;
import io.volunteerapp.volunteer_app.service.EventRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/event-registrations")
public class EventRegistrationController {

    private final EventRegistrationService registrationService;

    public EventRegistrationController(EventRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Register for an event (Authenticated users)
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<EventRegistrationResponse> registerForEvent(
            @Valid @RequestBody EventRegistrationRequest request) {
        EventRegistrationResponse response = registrationService.registerForEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get registrations for an event (Admin only)
     */
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<EventRegistrationResponse>> getEventRegistrations(
            @PathVariable Integer eventId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) String status) {

        PageResponse<EventRegistrationResponse> response = registrationService.getEventRegistrations(
                eventId, page, size, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current user's registrations (Authenticated users)
     */
    @GetMapping("/my-registrations")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PageResponse<EventRegistrationResponse>> getMyRegistrations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) String status) {

        PageResponse<EventRegistrationResponse> response = registrationService.getMyRegistrations(
                page, size, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel registration (Authenticated users can only cancel their own)
     */
    @DeleteMapping("/{registrationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Integer registrationId) {
        registrationService.cancelRegistration(registrationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update registration status (Admin only)
     */
    @PutMapping("/{registrationId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventRegistrationResponse> updateRegistrationStatus(
            @PathVariable Integer registrationId,
            @RequestParam String status) {

        EventRegistrationResponse response = registrationService.updateRegistrationStatus(
                registrationId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Check-in user for event (Admin only)
     */
    @PutMapping("/{registrationId}/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventRegistrationResponse> checkInUser(@PathVariable Integer registrationId) {
        EventRegistrationResponse response = registrationService.checkInUser(registrationId);
        return ResponseEntity.ok(response);
    }
}
