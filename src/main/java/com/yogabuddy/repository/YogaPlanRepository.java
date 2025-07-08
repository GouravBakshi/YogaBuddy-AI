package com.yogabuddy.repository;

import com.yogabuddy.entity.YogaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YogaPlanRepository extends JpaRepository<YogaPlan, Long> {
    Optional<YogaPlan> findTopByUserIdOrderByCreatedDateDesc(Long userId);
}

