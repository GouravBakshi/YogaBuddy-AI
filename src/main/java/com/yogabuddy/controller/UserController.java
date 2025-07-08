//package com.yogabuddy.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/user")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final UserService userService;
//
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser() {
//        return ResponseEntity.ok(userService.getCurrentUser());
//    }
//
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/plan")
//    public ResponseEntity<?> getYogaPlan() {
//        return ResponseEntity.ok(userService.getYogaPlanForUser());
//    }
//
//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/track/{day}")
//    public ResponseEntity<?> trackDay(@PathVariable int day) {
//        userService.markDayAsCompleted(day);
//        return ResponseEntity.ok("Progress saved for Day " + day);
//    }
//}
//
