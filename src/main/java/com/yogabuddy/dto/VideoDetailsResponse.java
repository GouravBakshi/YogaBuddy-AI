package com.yogabuddy.dto;


import lombok.Data;
import java.util.List;

@Data
public class VideoDetailsResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private ContentDetails contentDetails;
    }

    @Data
    public static class ContentDetails {
        private String duration; // ISO 8601
    }
}

