package io.volunteerapp.volunteer_app.repos;

import io.volunteerapp.volunteer_app.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRepository extends JpaRepository<Event, Integer> {
}
