package io.volunteerapp.volunteer_app.repos;

import io.volunteerapp.volunteer_app.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
}
