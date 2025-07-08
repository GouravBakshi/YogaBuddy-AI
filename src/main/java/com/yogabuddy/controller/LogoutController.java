package com.yogabuddy.controller;

import com.yogabuddy.entity.User;
import com.yogabuddy.repository.UserRepository;
import com.yogabuddy.security.JwtUtils;
import com.yogabuddy.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logout")
@RequiredArgsConstructor
public class LogoutController {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String email = jwtUtils.getEmailFromJwtToken(token);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            refreshTokenService.deleteByUser(user);

            return ResponseEntity.ok("Logout successful. Refresh token invalidated.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or missing Authorization header.");
        }
    }
}
