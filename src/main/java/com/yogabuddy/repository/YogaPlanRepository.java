package com.yogabuddy.repository;

import com.yogabuddy.entity.User;
import com.yogabuddy.entity.YogaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YogaPlanRepository extends JpaRepository<YogaPlan, Long> {
    Optional<YogaPlan> findTopByUserIdOrderByCreatedDateDesc(Long userId);

    List<YogaPlan> findByUserId(Long userId);

    List<YogaPlan> findAllByUser(User user);
}

