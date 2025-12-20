package io.volunteerapp.volunteer_app.repository;

import io.volunteerapp.volunteer_app.model.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardTypeRepository extends JpaRepository<RewardType, Integer> {
}
