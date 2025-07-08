package com.yogabuddy.repository;

import com.yogabuddy.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    List<UserProgress> findByUserIdOrderByDateAsc(Long userId);

    int countByUserIdAndCompletedTrue(Long userId);

    Optional<UserProgress> findByUserIdAndDate(Long userId, LocalDate date);
}

