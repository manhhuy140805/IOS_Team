package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.EventRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventResponse;
import io.volunteerapp.volunteer_app.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events with filters and pagination (Public access)
     */
    @GetMapping("")
    public ResponseEntity<PageResponse<EventResponse>> getAllEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "eventTypeId", required = false) Integer eventTypeId,
            @RequestParam(name = "startDateFrom", required = false) Date startDateFrom,
            @RequestParam(name = "startDateTo", required = false) Date startDateTo,
            @RequestParam(name = "hasReward", required = false) Boolean hasReward) {

        PageResponse<EventResponse> response = eventService.getAllEvents(
                page, size, sortBy, sortDirection, title, location, status,
                eventTypeId, startDateFrom, startDateTo, hasReward);
        return ResponseEntity.ok(response);
    }

    /**
     * Get event by ID (Public access)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Integer id) {
        EventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get events created by current user (Authenticated users)
     */
    @GetMapping("/my-events")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PageResponse<EventResponse>> getMyEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        PageResponse<EventResponse> response = eventService.getMyEvents(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    /**
     * Get events by user ID (Admin only)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByUserId(
            @PathVariable Integer userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<EventResponse> response = eventService.getEventsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new event (Admin only)
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update event (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Integer id,
            @Valid @RequestBody EventRequest eventRequest) {

        EventResponse response = eventService.updateEvent(id, eventRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete event (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
