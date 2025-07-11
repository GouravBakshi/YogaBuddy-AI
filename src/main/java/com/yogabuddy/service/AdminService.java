package com.yogabuddy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogabuddy.dto.DailyYogaPlanDTO;
import com.yogabuddy.dto.UserResponseDTO;
import com.yogabuddy.dto.YogaPlanDTO;
import com.yogabuddy.entity.DailyYogaPlan;
import com.yogabuddy.entity.User;
import com.yogabuddy.entity.YogaPlan;
import com.yogabuddy.repository.DailyYogaPlanRepository;
import com.yogabuddy.repository.UserRepository;
import com.yogabuddy.repository.YogaPlanRepository;
import com.yogabuddy.repository.YogaPoseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final YogaPlanRepository yogaPlanRepository;
    private final DailyYogaPlanRepository dailyYogaPlanRepository;
    private final YogaPoseRepository yogaPoseRepository;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Modifying
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<YogaPlan> plans = yogaPlanRepository.findAllByUser(user);
        for (YogaPlan plan : plans) {
            dailyYogaPlanRepository.deleteAllByYogaPlan(plan);
        }
        yogaPlanRepository.deleteAll(plans);
        userRepository.delete(user);
    }


    private UserResponseDTO mapToDto(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getName())
                .roles(Collections.singleton(user.getRole()))
                .build();
    }

    @Transactional
    public List<YogaPlanDTO> getAllYogaPlansByUser(Long userId) {
        List<YogaPlan> yogaPlans = yogaPlanRepository.findByUserId(userId);

        return yogaPlans.stream().map(plan -> {

            Long userIdInternal = plan.getUser().getId();

            // Fetch daily yoga plans manually
            List<DailyYogaPlan> dailyPlans = dailyYogaPlanRepository.findByYogaPlanIdWithPosesJson(plan.getId());

            // Ensure LOB stream is read before session closes
            for (DailyYogaPlan p : dailyPlans) {
                String poses = p.getPosesJson(); // Force access
            }

            List<DailyYogaPlanDTO> dailyPlanDTOs = dailyPlans.stream().map(daily -> {
                List<DailyYogaPlanDTO.YogaPoseEntry> poseEntries = new ArrayList<>();

                try {
                    // Parse posesJson: [{"poseName": "Sukhasana", "duration": 5}, ...]
                    JsonNode poseArray = new ObjectMapper().readTree(daily.getPosesJson());
                    for (JsonNode poseNode : poseArray) {
                        String poseName = poseNode.get("poseName").asText();
                        int duration = poseNode.get("duration").asInt();

                        poseEntries.add(
                                DailyYogaPlanDTO.YogaPoseEntry.builder()
                                        .poseName(poseName)
                                        .duration(duration)
                                        .build()
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse posesJson for DailyYogaPlan ID: " + daily.getId());
//                    e.printStackTrace();
                }

                return DailyYogaPlanDTO.builder()
                        .title(daily.getTitle())
                        .dayNumber(daily.getDayNumber())
                        .poses(poseEntries)
                        .build();
            }).toList();

            return YogaPlanDTO.builder()
                    .id(plan.getId())
                    .userId(userIdInternal)
                    .goal(plan.getGoal())
                    .issue(plan.getIssue())
                    .createdDate(plan.getCreatedDate())
                    .dailyPlans(dailyPlanDTOs)
                    .build();
        }).toList();
    }
}
