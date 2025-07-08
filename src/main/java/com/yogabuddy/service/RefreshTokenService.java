package com.yogabuddy.service;

import com.yogabuddy.entity.RefreshToken;
import com.yogabuddy.entity.User;
import com.yogabuddy.exception.RefreshTokenExpiredException;
import com.yogabuddy.repository.RefreshTokenRepository;
import com.yogabuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${refresh.expiry}")
    public long refreshTokenValidity;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username){

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expiry(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
        } else {
            refreshToken.setRefreshToken(UUID.randomUUID().toString()); // rotate token
            refreshToken.setExpiry(Instant.now().plusMillis(refreshTokenValidity));
        }

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("Given token does not exist."));

        if (refreshToken.getExpiry().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenExpiredException("Refresh token has expired.");
        }

        return refreshToken;
    }

    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setRefreshToken(UUID.randomUUID().toString());
        oldToken.setExpiry(Instant.now().plusMillis(refreshTokenValidity));
        return refreshTokenRepository.save(oldToken);
    }


    public void deleteByUser(User user) {
        RefreshToken token = user.getRefreshToken();
        if (token != null) {
            refreshTokenRepository.delete(token);
            user.setRefreshToken(null); // detach reference
            userRepository.save(user);
        }
    }

}
