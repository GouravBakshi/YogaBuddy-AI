package com.yogabuddy.service;

import com.yogabuddy.dto.VideoDetailsResponse;
import com.yogabuddy.dto.YouTubeResponse;
import com.yogabuddy.entity.YogaPose;
import com.yogabuddy.repository.YogaPoseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class YouTubeService {

    @Value("${youtube.api-key}")
    private String apiKey;

    @Value("${youtube.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final YogaPoseRepository yogaPoseRepository;

    public String getYogaVideoUrl(String poseName) {
        Optional<YogaPose> poseOptional = yogaPoseRepository.findByNameIgnoreCase(poseName);

        // ✅ Use cached video if available
        if (poseOptional.isPresent() && poseOptional.get().getVideoUrl() != null) {
            return poseOptional.get().getVideoUrl();
        }

        // 🔍 Search for top 5 videos
        String uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("part", "snippet")
                .queryParam("q", poseName + " yoga pose tutorial")
                .queryParam("maxResults", 5)
                .queryParam("type", "video")
                .queryParam("videoDuration", "short")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        YouTubeResponse response = restTemplate.getForObject(uri, YouTubeResponse.class);

        if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
            String fallbackVideoId = null;

            for (var item : response.getItems()) {
                String videoId = item.getId().getVideoId();

                if (videoId != null) {
                    if (isDurationGreaterThanOneMinute(videoId)) {
                        String url = "https://www.youtube.com/watch?v=" + videoId;
                        poseOptional.ifPresent(pose -> cachePoseVideo(pose, url));
                        return url;
                    }
                    if (fallbackVideoId == null) {
                        fallbackVideoId = videoId;
                    }
                }
            }

            // 🔁 Use fallback video if none >= 1 min
            if (fallbackVideoId != null) {
                String fallbackUrl = "https://www.youtube.com/watch?v=" + fallbackVideoId;
                poseOptional.ifPresent(pose -> cachePoseVideo(pose, fallbackUrl));
                return fallbackUrl;
            }
        }

        return null;
    }

    private void cachePoseVideo(YogaPose pose, String videoUrl) {
        pose.setVideoUrl(videoUrl);
        yogaPoseRepository.save(pose);
    }

    private boolean isDurationGreaterThanOneMinute(String videoId) {
        String url = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/youtube/v3/videos")
                .queryParam("part", "contentDetails")
                .queryParam("id", videoId)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        VideoDetailsResponse response = restTemplate.getForObject(url, VideoDetailsResponse.class);

        if (response != null && !response.getItems().isEmpty()) {
            String isoDuration = response.getItems().getFirst().getContentDetails().getDuration(); // e.g., PT3M45S
            Duration duration = Duration.parse(isoDuration);
            return duration.toMinutes() >= 1;
        }

        return false;
    }
}
