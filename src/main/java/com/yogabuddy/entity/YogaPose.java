package com.yogabuddy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YogaPose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String level; // Beginner/Intermediate
    private String category; // Strength, Relaxation, etc.
    private String benefits;
    private String precautions;

    @Column(name = "video_url")
    private String videoUrl;
    private int duration; // in minutes
}

