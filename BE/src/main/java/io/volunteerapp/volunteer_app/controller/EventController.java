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

    // quản lý sự kiện của organization - lấy events mà user hiện tại đã tạo
    @GetMapping("/organization/me")
    // @PreAuthorize("hasRole('ROLE_ORGANIZATION')")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByOrganization(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.oauth2.jwt.Jwt jwt,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        // Lấy email từ JWT token (sub claim)
        String email = jwt.getSubject();
        PageResponse<EventResponse> response = eventService.getEventsByCreatorEmail(email, page, size, sortBy,
                sortDirection);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Integer id) {
        EventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{eventTypeId}")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByType(
            @PathVariable Integer eventTypeId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        PageResponse<EventResponse> response = eventService.getEventsByType(
                eventTypeId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<EventResponse>> searchEvents(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        PageResponse<EventResponse> response = eventService.searchEvents(
                query, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-events")
    // @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PageResponse<EventResponse>> getMyEvents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        PageResponse<EventResponse> response = eventService.getMyEvents(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<EventResponse>> getEventsByUserId(
            @PathVariable Integer userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<EventResponse> response = eventService.getEventsByUserId(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest eventRequest) {
        EventResponse response = eventService.createEvent(eventRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Integer id,
            @Valid @RequestBody EventRequest eventRequest) {

        EventResponse response = eventService.updateEvent(id, eventRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEventStatus(
            @PathVariable Integer id,
            @RequestParam("status") String status) {
        EventResponse response = eventService.updateEventStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ai-search")
    public ResponseEntity<PageResponse<EventResponse>> searchEventsByAI(
            @Valid @RequestBody io.volunteerapp.volunteer_app.DTO.requeset.AiSearchRequest request) {
        PageResponse<EventResponse> response = eventService.searchEventsByAI(request.getQuery());
        return ResponseEntity.ok(response);
    }
}
