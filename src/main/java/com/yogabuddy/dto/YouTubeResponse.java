package com.yogabuddy.dto;


import lombok.Data;
import java.util.List;

@Data
public class YouTubeResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Id id;
    }

    @Data
    public static class Id {
        private String videoId;
    }
}
