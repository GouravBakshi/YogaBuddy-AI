package com.yogabuddy.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YogaPlanDTO {
    private Long id;
    private Long userId;
    private String goal;
    private String issue;
    private LocalDate createdDate;
    private List<DailyYogaPlanDTO> dailyPlans;
}
