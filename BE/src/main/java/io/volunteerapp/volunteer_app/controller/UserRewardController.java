package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.PageResponse;
import io.volunteerapp.volunteer_app.DTO.requeset.UserRewardRequest;
import io.volunteerapp.volunteer_app.DTO.response.UserRewardResponse;
import io.volunteerapp.volunteer_app.service.UserRewardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-rewards")
public class UserRewardController {

    private final UserRewardService userRewardService;

    public UserRewardController(UserRewardService userRewardService) {
        this.userRewardService = userRewardService;
    }

    /**
     * Claim a reward (Authenticated users)
     */
    @PostMapping("/claim")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ADMIN')")
    public ResponseEntity<UserRewardResponse> claimReward(@Valid @RequestBody UserRewardRequest userRewardRequest) {
        UserRewardResponse response = userRewardService.claimReward(userRewardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get current user's rewards (Authenticated users)
     */
    @GetMapping("/my-rewards")
    @PreAuthorize("hasAnyRole('ROLE_VOLUNTEER', 'ROLE_ADMIN')")
    public ResponseEntity<PageResponse<UserRewardResponse>> getMyRewards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<UserRewardResponse> response = userRewardService.getMyRewards(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get pending rewards (Admin only)
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<UserRewardResponse>> getPendingRewards(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<UserRewardResponse> response = userRewardService.getPendingRewards(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user rewards by user ID (Admin only)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<UserRewardResponse>> getUserRewards(
            @PathVariable Integer userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<UserRewardResponse> response = userRewardService.getUserRewards(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Update reward status (Admin only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserRewardResponse> updateRewardStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        UserRewardResponse response = userRewardService.updateRewardStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
