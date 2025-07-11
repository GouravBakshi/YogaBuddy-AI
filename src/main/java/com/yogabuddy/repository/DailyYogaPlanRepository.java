package com.yogabuddy.repository;

import com.yogabuddy.entity.DailyYogaPlan;
import com.yogabuddy.entity.YogaPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyYogaPlanRepository extends JpaRepository<DailyYogaPlan, Long> {
    List<DailyYogaPlan> findByYogaPlanIdOrderByDayNumber(Long yogaPlanId);

    @Query("SELECT d FROM DailyYogaPlan d JOIN FETCH d.yogaPlan WHERE d.yogaPlan.id = :yogaPlanId")
    List<DailyYogaPlan> findByYogaPlanIdWithPosesJson(@Param("yogaPlanId") Long yogaPlanId);

    void deleteAllByYogaPlan(YogaPlan plan);
}

