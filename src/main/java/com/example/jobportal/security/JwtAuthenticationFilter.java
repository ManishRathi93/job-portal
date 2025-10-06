package com.example.jobportal.security;


import com.example.jobportal.entity.User;
import com.example.jobportal.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Get Authorization header
            String authHeader = request.getHeader("Authorization");

            // Check if header exists and starts with "Bearer "
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // Extract JWT token (remove "Bearer " prefix)
                String jwt = authHeader.substring(7);

                // Validate token
                if (jwtUtil.validateToken(jwt)) {
                    // Get email from token
                    String email = jwtUtil.getEmailFromToken(jwt);

                    // Find user in database
                    User user = userRepository.findByEmail(email);

                    if (user == null) {
                        throw new RuntimeException("User not found");
                    }

                    if (user != null) {
                        // Create authentication token for Spring Security
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        new ArrayList<>() // No roles for now, just basic auth
                                );

                        // Set authentication details
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Tell Spring Security this user is authenticated
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        log.info("User {} authenticated successfully", email);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Continue with the request
        filterChain.doFilter(request, response);
    }
}
