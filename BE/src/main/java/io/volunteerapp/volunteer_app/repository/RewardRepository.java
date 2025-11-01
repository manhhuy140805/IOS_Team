package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Integer> {
}
