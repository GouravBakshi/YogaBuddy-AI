package com.yogabuddy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private int age;
    private String gender;
    private String fitnessGoal; // e.g., "Flexibility"
    private String issue;       // e.g., "Back Pain"
    private String experienceLevel; // Beginner/Intermediate
}
