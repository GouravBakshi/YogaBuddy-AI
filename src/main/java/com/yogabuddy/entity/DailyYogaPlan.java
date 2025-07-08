package com.yogabuddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyYogaPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "yoga_plan_id")
    private YogaPlan yogaPlan;

    private int dayNumber; // 1 to 30

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String posesJson; // store pose IDs and durations in JSON
}

