package com.taskwell.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import com.taskwell.service.CustomUserDetailsService;

import com.taskwell.repository.UserRepository;

import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // Allow registration
                        .requestMatchers(HttpMethod.GET, "/api/users/verify/**").permitAll() // Allow email verification
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll() // Allow public access to user
                                                                                        // by ID
                        .requestMatchers(HttpMethod.GET, "/api/users/username/**").permitAll() // Allow public access to
                                                                                               // user by username
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/role").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/toggle-lock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").authenticated()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allow
                                                                                                              // Swagger
                        .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }
}
