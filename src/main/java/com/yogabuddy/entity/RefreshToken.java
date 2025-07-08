package com.yogabuddy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;

    private String refreshToken;

    private Instant expiry;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;
}
