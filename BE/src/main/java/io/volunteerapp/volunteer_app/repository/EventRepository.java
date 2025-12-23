package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.Event;
import io.volunteerapp.volunteer_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
    List<Event> findByCreator(User creator);
}
