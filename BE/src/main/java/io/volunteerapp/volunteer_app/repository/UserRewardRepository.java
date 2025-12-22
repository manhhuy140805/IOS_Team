package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.UserReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRewardRepository extends JpaRepository<UserReward, Integer> {
    Page<UserReward> findByStatus(String status, Pageable pageable);
}
