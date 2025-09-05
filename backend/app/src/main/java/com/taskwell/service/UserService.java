package com.taskwell.service;

import com.taskwell.repository.UserRepository;
import com.taskwell.utils.ValidationUtils;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.taskwell.model.User;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Register new user
    public User registerUser(User user) {
        logger.info("Registering user: " + user.getUsername());

        // Hash password
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        // Check username and email validity
        if (!ValidationUtils.isValidUsername(user.getUsername())) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check if username or email already exists
        if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Username or email already taken");
        }

        // Check password strength
        if (!ValidationUtils.isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException("Password does not meet strength requirements");
        }

        logger.info("User registered: " + user.getUsername());
        return userRepository.save(user);
    }

    // Find user by ID, username, or email
    public User findByID(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Update user details
    public User changeUsername(Long id, String newUsername) {
        User user = findByID(id);
        if (user != null && !isUsernameTaken(newUsername)) {
            if (!ValidationUtils.isValidUsername(newUsername)) {
                throw new IllegalArgumentException("Invalid username");
            }
            user.setUsername(newUsername);
            logger.info("Username changed for user ID: " + id + " to new username: " + newUsername);
            return userRepository.save(user);
        }
        logger.error("Username change failed for user ID: " + id + ". Was the username taken? "
                + isUsernameTaken(newUsername));
        return null;
    }

    public User changeEmail(Long id, String newEmail) {
        User user = findByID(id);
        if (user != null && !isEmailTaken(newEmail)) {
            if (!ValidationUtils.isValidEmail(newEmail)) {
                throw new IllegalArgumentException("Invalid email format");
            }
            user.setEmail(newEmail);
            logger.info("Email changed for user: " + user.getUsername() + " to new email: " + newEmail);
            return userRepository.save(user);
        }
        logger.error("Email change failed for user ID: " + id + ". Was the email taken? " + isEmailTaken(newEmail));
        return null;
    }

    @Transactional
    public User changeRole(Long id, String newRole) {
        User user = findByID(id);
        if (user != null) {
            user.setRole(newRole);
            logger.info("Set role " + newRole + " for user: " + user.getUsername());
            return userRepository.save(user);
        }
        logger.error("Role change failed for user ID: " + id + ". User not found.");
        return null;
    }

    // Delete user account
    public boolean deleteUser(Long id) {
        User user = findByID(id);
        if (user != null) {
            userRepository.delete(user);
            logger.info("User deleted: " + user.getUsername());
            return true;
        }
        logger.error("User deletion failed for user ID: " + id + ". User not found.");
        return false;
    }

    @Transactional
    // Lock/unlock user account
    public void toggleUserLocked(Long id) {
        User user = findByID(id);
        if (user != null) {
            user.setLocked(!user.isLocked());
            logger.info("User " + (user.isLocked() ? "locked: " : "unlocked: ") + user.getUsername());
            userRepository.save(user);
        }
    }

    @Transactional
    // Enable/disable user account
    public void toggleUserEnabled(Long id) {
        User user = findByID(id);
        if (user != null) {
            user.setEnabled(!user.isEnabled());
            logger.info("User " + (user.isEnabled() ? "enabled: " : "disabled: ") + user.getUsername());
            userRepository.save(user);
        }
    }

    // Assign roles to users
    public void assignRole(Long id, String role) {
        User user = findByID(id);
        if (user != null) {
            user.setRole(role);
            logger.info("Assigned role " + role + " to user: " + user.getUsername());
            userRepository.save(user);
        }
    }

    // Check if user or email is already taken
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // List all users
    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    // Handle user verification (TODO)

    // Change / reset password
    public User changePassword(Long id, String newPassword) {
        User user = findByID(id);
        if (user != null) {
            if (!ValidationUtils.isValidPassword(newPassword)) {
                throw new IllegalArgumentException("Password does not meet strength requirements");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            logger.info("Password changed for user: " + user.getUsername());
            return userRepository.save(user);
        }
        logger.error("Password change failed for user ID: " + id + ". User not found.");
        return null;
    }
}
