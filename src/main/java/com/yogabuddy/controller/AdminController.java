//package com.yogabuddy.controller;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final AdminService adminService;
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/users")
//    public ResponseEntity<?> getAllUsers() {
//        return ResponseEntity.ok(adminService.getAllUsers());
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/user/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        adminService.deleteUserById(id);
//        return ResponseEntity.ok("User deleted successfully");
//    }
//}
