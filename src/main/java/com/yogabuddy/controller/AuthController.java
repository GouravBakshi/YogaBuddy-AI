package com.yogabuddy.controller;

import com.yogabuddy.dto.RefreshTokenRequest;
import com.yogabuddy.entity.RefreshToken;
import com.yogabuddy.entity.User;
import com.yogabuddy.dto.LoginRequest;
import com.yogabuddy.dto.RegisterRequest;
import com.yogabuddy.security.JwtAuthenticationResponse;
import com.yogabuddy.security.JwtUtils;
import com.yogabuddy.service.RefreshTokenService;
import com.yogabuddy.service.UserDetailsImpl;
import com.yogabuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());


        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt,refreshToken.getRefreshToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshJwtToken(@RequestBody RefreshTokenRequest request){

        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = oldToken.getUser();

        RefreshToken newToken = refreshTokenService.rotateRefreshToken(oldToken);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        String token = this.jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthenticationResponse(token,newToken.getRefreshToken()));

    }


}
