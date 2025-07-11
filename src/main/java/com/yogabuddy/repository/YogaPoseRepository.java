package com.yogabuddy.repository;

import com.yogabuddy.entity.YogaPose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YogaPoseRepository extends JpaRepository<YogaPose, Long> {
    Optional<YogaPose> findByNameIgnoreCase(String name);

    @NonNull
    Optional<YogaPose> findById(@NonNull Long id);
}
