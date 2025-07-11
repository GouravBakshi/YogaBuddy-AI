package com.yogabuddy.controller;

import com.yogabuddy.entity.YogaPose;
import com.yogabuddy.service.YogaPoseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pose")
@RequiredArgsConstructor
public class YogaPoseController {


    private final YogaPoseService yogaPoseService;

    @GetMapping("/all")
    public ResponseEntity<List<YogaPose>> getAllPoses() {
        return ResponseEntity.ok(yogaPoseService.getAllPoses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<YogaPose> getPoseById(@PathVariable Long id) {
        return yogaPoseService.getPoseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<YogaPose> addPose(@RequestBody YogaPose pose) {
        return ResponseEntity.ok(yogaPoseService.createPose(pose));
    }

    @PostMapping("/add-multiple")
    public ResponseEntity<List<YogaPose>> addMultiplePoses(@RequestBody List<YogaPose> poses) {
        return ResponseEntity.ok(yogaPoseService.createMultiplePoses(poses));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePose(@PathVariable Long id) {
        yogaPoseService.deletePose(id);
        return ResponseEntity.noContent().build();
    }
}
