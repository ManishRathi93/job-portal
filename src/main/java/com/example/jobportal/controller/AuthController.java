package com.example.jobportal.controller;

import com.example.jobportal.entity.User;
import com.example.jobportal.security.JwtUtil;
import com.example.jobportal.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(new AuthResponse(
                    "User registered successfully!",
                    null,
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getUserType().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request.getEmail(), request.getPassword());

            // Get user details for response
            User user = userService.getUserByEmail(request.getEmail());

            return ResponseEntity.ok(new AuthResponse(
                    "Login successful!",
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getUserType().toString()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix
            String jwtToken = token.substring(7);

            if (!jwtUtil.validateToken(jwtToken)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Invalid token"));
            }

            String email = jwtUtil.getEmailFromToken(jwtToken);
            User user = userService.getUserByEmail(email);

            return ResponseEntity.ok(new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getUserType().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid token"));
        }
    }
}

@Data
class AuthResponse {
    private String message;
    private String token;
    private Long userId;
    private String email;
    private String userType;

    public AuthResponse(String message, String token, Long userId, String email, String userType) {
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }
}

@Data
class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String userType;

    public UserResponse(Long id, String email, String fullName, String userType) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.userType = userType;
    }
}

@Data
class ErrorResponse {
    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}

@Data
class LoginRequest {
    private String email;
    private String password;
}
