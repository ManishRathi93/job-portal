package com.example.jobportal.service;

import com.example.jobportal.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /**
     * Get current authenticated user from Spring Security context
     * This is much simpler now that we have JWT filter!
     */
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        throw new RuntimeException("User not authenticated");
    }

    /**
     * Get current user ID
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}