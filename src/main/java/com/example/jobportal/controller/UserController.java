package com.example.jobportal.controller;

import com.example.jobportal.entity.User;
import com.example.jobportal.service.AuthService;
import com.example.jobportal.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // Public endpoints - for backward compatibility (you can remove these later)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully with ID: " + savedUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Protected endpoints - require authentication
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            User currentUser = authService.getCurrentUser();

            // Don't return password in response
            User safeUser = new User();
            safeUser.setId(currentUser.getId());
            safeUser.setEmail(currentUser.getEmail());
            safeUser.setFullName(currentUser.getFullName());
            safeUser.setUserType(currentUser.getUserType());
            safeUser.setCreatedAt(currentUser.getCreatedAt());

            return ResponseEntity.ok(safeUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            User currentUser = authService.getCurrentUser();

            // Update allowed fields
            currentUser.setFullName(request.getFullName());
            // Don't allow email or password changes here (should be separate endpoints)

            userService.updateUser(currentUser);
            return ResponseEntity.ok("Profile updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin endpoints (you can add role-based security later)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            User currentUser = authService.getCurrentUser();
            // For now, any authenticated user can see this (you can add admin role later)

            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            // Ensure user is authenticated
            authService.getCurrentUser();

            Optional<User> user = userService.getUserById(id);
            return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

// Request DTO for profile updates
@Data
class UpdateProfileRequest {
    private String fullName;
    // Add other fields as needed (phone, skills, etc.)
}