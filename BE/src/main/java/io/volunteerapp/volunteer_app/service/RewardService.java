package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.RewardRequest;
import io.volunteerapp.volunteer_app.DTO.response.RewardResponse;
import io.volunteerapp.volunteer_app.mapper.RewardMapper;
import io.volunteerapp.volunteer_app.model.Reward;
import io.volunteerapp.volunteer_app.repository.RewardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardMapper rewardMapper;

    public RewardService(RewardRepository rewardRepository, RewardMapper rewardMapper) {
        this.rewardRepository = rewardRepository;
        this.rewardMapper = rewardMapper;
    }

    // Get all rewards with pagination
    public PageResponse<RewardResponse> getAllRewards(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Reward> rewardPage = rewardRepository.findAll(pageable);
        List<RewardResponse> responses = rewardPage.getContent().stream()
                .map(rewardMapper::toResponse)
                .toList();

        return new PageResponse<>(
                responses,
                rewardPage.getNumber(),
                rewardPage.getTotalElements(),
                rewardPage.getTotalPages());
    }

    // Get reward by ID
    public RewardResponse getRewardById(Integer id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with id: " + id));
        return rewardMapper.toResponse(reward);
    }

    // Create new reward (Admin only)
    @Transactional
    public RewardResponse createReward(RewardRequest rewardRequest) {
        Reward reward = rewardMapper.toEntity(rewardRequest);
        Reward savedReward = rewardRepository.save(reward);
        return rewardMapper.toResponse(savedReward);
    }

    // Update reward (Admin only)
    @Transactional
    public RewardResponse updateReward(Integer id, RewardRequest rewardRequest) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with id: " + id));

        rewardMapper.updateEntityFromRequest(rewardRequest, reward);
        Reward updatedReward = rewardRepository.save(reward);
        return rewardMapper.toResponse(updatedReward);
    }

    // Delete reward (Admin only)
    @Transactional
    public void deleteReward(Integer id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found with id: " + id));
        rewardRepository.delete(reward);
    }
}
