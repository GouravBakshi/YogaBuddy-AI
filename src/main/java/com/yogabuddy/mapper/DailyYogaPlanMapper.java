package com.yogabuddy.mapper;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogabuddy.dto.DailyYogaPlanDTO;
import com.yogabuddy.entity.DailyYogaPlan;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class DailyYogaPlanMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Convert Entity -> DTO
    public static DailyYogaPlanDTO toDTO(DailyYogaPlan entity) {
        List<DailyYogaPlanDTO.YogaPoseEntry> poses = null;

        try {
            poses = objectMapper.readValue(
                    entity.getPosesJson(),
                    new TypeReference<List<DailyYogaPlanDTO.YogaPoseEntry>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON in posesJson", e);
        }

        return DailyYogaPlanDTO.builder()
                .dayNumber(entity.getDayNumber())
                .poses(poses)
                .build();
    }

    // Convert DTO -> Entity
    public static DailyYogaPlan toEntity(DailyYogaPlanDTO dto) {
        String posesJson;
        try {
            posesJson = objectMapper.writeValueAsString(dto.getPoses());
        } catch (IOException e) {
            throw new RuntimeException("Error serializing poses to JSON", e);
        }

        DailyYogaPlan plan = new DailyYogaPlan();
        plan.setDayNumber(dto.getDayNumber());
        plan.setPosesJson(posesJson);
        return plan;
    }
}
