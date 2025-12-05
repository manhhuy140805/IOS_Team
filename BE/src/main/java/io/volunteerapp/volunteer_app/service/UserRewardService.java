package io.volunteerapp.volunteer_app.service;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.UserRewardRequest;
import io.volunteerapp.volunteer_app.DTO.response.UserRewardResponse;
import io.volunteerapp.volunteer_app.mapper.UserRewardMapper;
import io.volunteerapp.volunteer_app.model.Reward;
import io.volunteerapp.volunteer_app.model.User;
import io.volunteerapp.volunteer_app.model.UserReward;
import io.volunteerapp.volunteer_app.repository.RewardRepository;
import io.volunteerapp.volunteer_app.repository.UserRepository;
import io.volunteerapp.volunteer_app.repository.UserRewardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRewardService {

        private final UserRewardRepository userRewardRepository;
        private final UserRepository userRepository;
        private final RewardRepository rewardRepository;
        private final UserRewardMapper userRewardMapper;

        public UserRewardService(UserRewardRepository userRewardRepository,
                        UserRepository userRepository,
                        RewardRepository rewardRepository,
                        UserRewardMapper userRewardMapper) {
                this.userRewardRepository = userRewardRepository;
                this.userRepository = userRepository;
                this.rewardRepository = rewardRepository;
                this.userRewardMapper = userRewardMapper;
        }

        // Claim a reward (Authenticated users)
        @Transactional
        public UserRewardResponse claimReward(UserRewardRequest userRewardRequest) {
                User currentUser = getCurrentUser();

                // Get reward
                Reward reward = rewardRepository.findById(userRewardRequest.getRewardId())
                                .orElseThrow(
                                                () -> new RuntimeException("Reward not found with id: "
                                                                + userRewardRequest.getRewardId()));

                // Check if reward is available
                if (reward.getQuantity() <= 0) {
                        throw new RuntimeException("Reward is out of stock");
                }

                // Check if user has enough points
                if (currentUser.getTotalPoints() < reward.getPointsRequired()) {
                        throw new RuntimeException("Not enough points to claim this reward");
                }

                // Create user reward
                UserReward userReward = new UserReward();
                userReward.setUser(currentUser);
                userReward.setReward(reward);
                userReward.setStatus("PENDING"); // Default status
                userReward.setPointsSpent(reward.getPointsRequired());
                if (userRewardRequest.getNotes() != null) {
                        userReward.setNotes(userRewardRequest.getNotes());
                }

                // Deduct points from user
                currentUser.setTotalPoints(currentUser.getTotalPoints() - reward.getPointsRequired());
                userRepository.save(currentUser);

                // Decrease reward quantity
                reward.setQuantity(reward.getQuantity() - 1);
                rewardRepository.save(reward);

                UserReward savedUserReward = userRewardRepository.save(userReward);
                return userRewardMapper.toResponse(savedUserReward);
        }

        // Get current user's rewards with pagination
        public PageResponse<UserRewardResponse> getMyRewards(int page, int size) {
                User currentUser = getCurrentUser();

                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<UserReward> userRewardPage = userRewardRepository.findAll(pageable);

                // Filter by current user
                List<UserRewardResponse> responses = userRewardPage.getContent().stream()
                                .filter(ur -> ur.getUser().getId().equals(currentUser.getId()))
                                .map(userRewardMapper::toResponse)
                                .toList();

                return new PageResponse<>(
                                responses,
                                userRewardPage.getNumber(),
                                userRewardPage.getTotalElements(),
                                userRewardPage.getTotalPages());
        }

        // Get user rewards by user ID (Admin only)
        public PageResponse<UserRewardResponse> getUserRewards(Integer userId, int page, int size) {
                // Verify user exists
                userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

                Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
                Page<UserReward> userRewardPage = userRewardRepository.findAll(pageable);

                // Filter by user ID
                List<UserRewardResponse> responses = userRewardPage.getContent().stream()
                                .filter(ur -> ur.getUser().getId().equals(userId))
                                .map(userRewardMapper::toResponse)
                                .toList();

                return new PageResponse<>(
                                responses,
                                userRewardPage.getNumber(),
                                userRewardPage.getTotalElements(),
                                userRewardPage.getTotalPages());
        }

        // Update reward status (Admin only)
        @Transactional
        public UserRewardResponse updateRewardStatus(Integer id, String status) {
                UserReward userReward = userRewardRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("User reward not found with id: " + id));

                userReward.setStatus(status);
                UserReward updatedUserReward = userRewardRepository.save(userReward);
                return userRewardMapper.toResponse(updatedUserReward);
        }

        // Get current authenticated user
        private User getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName();
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
        }
}
