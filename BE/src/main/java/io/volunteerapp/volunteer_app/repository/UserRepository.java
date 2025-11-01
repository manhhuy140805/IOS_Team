package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
