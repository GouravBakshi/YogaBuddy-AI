package com.yogabuddy.repository;

import com.yogabuddy.entity.DailyYogaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyYogaPlanRepository extends JpaRepository<DailyYogaPlan, Long> {
    List<DailyYogaPlan> findByYogaPlanIdOrderByDayNumber(Long yogaPlanId);
}

