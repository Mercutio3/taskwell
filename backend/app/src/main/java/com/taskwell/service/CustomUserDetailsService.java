package com.taskwell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskwell.model.User;
import com.taskwell.repository.UserRepository;
import com.taskwell.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        logger.info("User found: {} (enabled: {}, locked: {}, verified: {})", user.getUsername(), !user.isLocked(),
                user.isLocked(), user.isVerified());
        return new CustomUserDetails(user);
    }
}
