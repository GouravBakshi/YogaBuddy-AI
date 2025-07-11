package com.yogabuddy.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyYogaPlanDTO {
    private String title;
    private int dayNumber;
    private List<YogaPoseEntry> poses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class YogaPoseEntry {
        private String poseName;
        private int duration; // in minutes
    }
}
