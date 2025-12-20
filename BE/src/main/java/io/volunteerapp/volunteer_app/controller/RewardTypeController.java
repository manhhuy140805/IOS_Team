package io.volunteerapp.volunteer_app.controller;

import io.volunteerapp.volunteer_app.DTO.response.RewardTypeResponse;
import io.volunteerapp.volunteer_app.service.RewardTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reward-types")
public class RewardTypeController {

    private final RewardTypeService rewardTypeService;

    public RewardTypeController(RewardTypeService rewardTypeService) {
        this.rewardTypeService = rewardTypeService;
    }

    /**
     * Get all reward types (Public access)
     */
    @GetMapping("")
    public ResponseEntity<List<RewardTypeResponse>> getAllRewardTypes() {
        List<RewardTypeResponse> response = rewardTypeService.getAllRewardTypes();
        return ResponseEntity.ok(response);
    }
}
