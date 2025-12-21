package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.requeset.EventTypeRequest;
import io.volunteerapp.volunteer_app.DTO.response.EventTypeResponse;
import io.volunteerapp.volunteer_app.service.EventTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event-types")
public class EventTypeController {

    private final EventTypeService eventTypeService;

    public EventTypeController(EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    /**
     * Get all event types (Public access)
     */
    @GetMapping("")
    public ResponseEntity<List<EventTypeResponse>> getAllEventTypes() {
        List<EventTypeResponse> response = eventTypeService.getAllEventTypes();
        return ResponseEntity.ok(response);
    }

    /**
     * Get event type by ID (Public access)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventTypeResponse> getEventTypeById(@PathVariable Integer id) {
        EventTypeResponse response = eventTypeService.getEventTypeById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new event type (Admin only)
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EventTypeResponse> createEventType(@Valid @RequestBody EventTypeRequest eventTypeRequest) {
        EventTypeResponse response = eventTypeService.createEventType(eventTypeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update event type (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EventTypeResponse> updateEventType(
            @PathVariable Integer id,
            @Valid @RequestBody EventTypeRequest eventTypeRequest) {
        EventTypeResponse response = eventTypeService.updateEventType(id, eventTypeRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete event type (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteEventType(@PathVariable Integer id) {
        eventTypeService.deleteEventType(id);
        return ResponseEntity.noContent().build();
    }
}
