package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.EventRegistration;
import io.volunteerapp.volunteer_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Integer> {

    // Count registrations for an event
    long countByEvent(Event event);

    // Check if user already registered for event
    boolean existsByUserAndEvent(User user, Event event);

    // Find registrations by event
    Page<EventRegistration> findByEvent(Event event, Pageable pageable);

    // Find registrations by event and status
    Page<EventRegistration> findByEventAndStatus(Event event, String status, Pageable pageable);

    // Find registrations by user
    Page<EventRegistration> findByUser(User user, Pageable pageable);

    // Find registrations by user and status
    Page<EventRegistration> findByUserAndStatus(User user, String status, Pageable pageable);
}
