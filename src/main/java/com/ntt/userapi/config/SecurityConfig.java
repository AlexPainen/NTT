package com.ntt.userapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration and @EnableWebSecurity enable Spring Security configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define the PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Basic Security Filter Chain config - allow POST to /api/users publicly
    // For a real app, you'd secure other endpoints and potentially use JWT authentication here
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API
                .authorizeHttpRequests(auth -> auth
                        // Allow POST requests to /api/users without authentication
                        .requestMatchers("/api/users").permitAll()
                        // You would secure other paths here, e.g.,
                        // .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().authenticated() // Default to authenticated for other requests
                );
        // Add session management or JWT filter here if implementing full auth

        return http.build();
    }
}