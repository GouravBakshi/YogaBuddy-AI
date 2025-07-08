package com.yogabuddy.controller;

import com.yogabuddy.service.YouTubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/yoga")
@RequiredArgsConstructor
public class YogaVideoController {

    private final YouTubeService youTubeService;

    @GetMapping("/video")
    public ResponseEntity<?> getVideo(@RequestParam String pose) {
        String videoUrl = youTubeService.getYogaVideoUrl(pose);
        if (videoUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("videoUrl", videoUrl));
    }
}
