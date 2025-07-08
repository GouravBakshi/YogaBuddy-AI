package com.yogabuddy.controller;

import com.yogabuddy.dto.PlanRequest;
import com.yogabuddy.entity.User;
import com.yogabuddy.repository.UserRepository;
import com.yogabuddy.service.UserDetailsImpl;
import com.yogabuddy.service.YogaAiPlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/yoga-plans")
@RequiredArgsConstructor
public class YogaPlanController {

    private final YogaAiPlannerService yogaAiPlannerService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    public ResponseEntity<?> generateYogaPlan(@RequestBody PlanRequest request,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        yogaAiPlannerService.generateAndSavePlan(request.getGoal(), request.getIssue(), user);

        return ResponseEntity.ok("Personalized yoga plan generated and saved!");
    }

    // (Optional) Add endpoints to fetch user’s plans later
}
