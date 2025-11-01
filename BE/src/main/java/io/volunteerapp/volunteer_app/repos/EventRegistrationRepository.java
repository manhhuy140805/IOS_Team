package io.volunteerapp.volunteer_app.repos;

import io.volunteerapp.volunteer_app.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Integer> {
}
