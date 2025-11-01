package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRewardRepository extends JpaRepository<UserReward, Integer> {
}
