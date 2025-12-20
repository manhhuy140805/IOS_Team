package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.response.RewardTypeResponse;
import io.volunteerapp.volunteer_app.mapper.RewardTypeMapper;
import io.volunteerapp.volunteer_app.model.RewardType;
import io.volunteerapp.volunteer_app.repository.RewardTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RewardTypeService {

    private final RewardTypeRepository rewardTypeRepository;
    private final RewardTypeMapper rewardTypeMapper;

    public RewardTypeService(RewardTypeRepository rewardTypeRepository, RewardTypeMapper rewardTypeMapper) {
        this.rewardTypeRepository = rewardTypeRepository;
        this.rewardTypeMapper = rewardTypeMapper;
    }

    // Get all reward types
    public List<RewardTypeResponse> getAllRewardTypes() {
        List<RewardType> rewardTypes = rewardTypeRepository.findAll();
        return rewardTypes.stream()
                .map(rewardTypeMapper::toResponse)
                .toList();
    }
}
