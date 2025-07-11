package com.yogabuddy.service;

import com.yogabuddy.entity.YogaPose;
import com.yogabuddy.repository.YogaPoseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YogaPoseService {

    @Autowired
    private YogaPoseRepository yogaPoseRepository;

    public List<YogaPose> getAllPoses() {
        return yogaPoseRepository.findAll();
    }

    public Optional<YogaPose> getPoseById(Long id) {
        return yogaPoseRepository.findById(id);
    }

    public YogaPose createPose(YogaPose pose) {
        return yogaPoseRepository.save(pose);
    }

    public void deletePose(Long id) {
        yogaPoseRepository.deleteById(id);
    }

    public List<YogaPose> createMultiplePoses(List<YogaPose> poses) {
        return yogaPoseRepository.saveAll(poses);
    }

}

