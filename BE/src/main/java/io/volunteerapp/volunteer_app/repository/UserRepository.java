package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Tìm user theo email (dùng cho login và kiểm tra duplicate)
    Optional<User> findByEmail(String email);
}
