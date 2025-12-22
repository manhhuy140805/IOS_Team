package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.RewardRequest;
import io.volunteerapp.volunteer_app.DTO.response.RewardResponse;
import io.volunteerapp.volunteer_app.service.RewardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Get all rewards with pagination (Public access)
     * Can filter by reward type ID using optional parameter
     */
    @GetMapping("")
    public ResponseEntity<PageResponse<RewardResponse>> getAllRewards(
            @RequestParam(name = "rewardTypeId", required = false) Integer rewardTypeId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {

        PageResponse<RewardResponse> response;
        if (rewardTypeId != null) {
            response = rewardService.getRewardsByTypeId(rewardTypeId, page, size, sortBy, sortDirection);
        } else {
            response = rewardService.getAllRewards(page, size, sortBy, sortDirection);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get reward by ID (Public access)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RewardResponse> getRewardById(@PathVariable Integer id) {
        RewardResponse response = rewardService.getRewardById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Create new reward (Admin only)
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RewardResponse> createReward(@Valid @RequestBody RewardRequest rewardRequest) {
        RewardResponse response = rewardService.createReward(rewardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update reward (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RewardResponse> updateReward(
            @PathVariable Integer id,
            @Valid @RequestBody RewardRequest rewardRequest) {

        RewardResponse response = rewardService.updateReward(id, rewardRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete reward (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReward(@PathVariable Integer id) {
        rewardService.deleteReward(id);
        return ResponseEntity.noContent().build();
    }
}
