package com.taskwell.service;

import com.taskwell.repository.UserRepository;
import com.taskwell.utils.SecurityUtils;
import com.taskwell.utils.ValidationUtils;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.taskwell.model.User;
import com.taskwell.model.UserRole;
import com.taskwell.dto.UserRegistrationRequest;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.taskwell.security.CustomUserDetails;

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

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        // Check username and email validity
        if (!ValidationUtils.isValidUsername(user.getUsername())) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check password strength
        if (!ValidationUtils.isValidPassword(user.getPassword())) {
            throw new IllegalArgumentException(
                    "Password must have at least 8 characters, one uppercase letter, one lowercase letter, one digit, and one special character");
        }

        // Hash password
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Check if username or email already exists
        if (isUsernameTaken(user.getUsername()) || isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Username or email already taken");
        }

        // Generate verification token
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerified(false);

        logger.info("User registered: {} (verification token generated)", user.getUsername());
        return userRepository.save(user);
    }

    public User registerUserFromDto(UserRegistrationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(UserRole.USER); // Set default role
        user.setVerified(false);
        return registerUser(user);
    }

    /**
     * Verifies a user by their verification token. If valid, sets verified=true and
     * clears the token.
     * 
     * @param token the verification token
     * @return true if verification succeeded, false otherwise
     */
    @Transactional
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            logger.info("User {} verified successfully", user.getUsername());
            return true;
        }
        logger.warn("Verification failed: token not found");
        return false;
    }

    @Transactional
    public void setVerified(Long userId, boolean verified) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setVerified(verified);
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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
        if (!SecurityUtils.isVerifiedUser() && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("User must be verified");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Update user details
    @Transactional
    public User changeUsername(Long id, String newUsername) {
        if (!SecurityUtils.isAdmin()) {
            // Only allow if the authenticated user is verified and is changing their own
            // username
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if (!principal.isVerified() || !principal.getId().equals(id)) {
                throw new AccessDeniedException("Forbidden");
            }
        }

        User user = findByID(id);
        if (ValidationUtils.isValidUsername(newUsername)) {
            if (!isUsernameTaken(newUsername, id)) {
                user.setUsername(newUsername);
                logger.info("Username changed for user ID: {} to new username: {}", id, newUsername);
                return userRepository.save(user);
            }
            logger.warn("Username change failed for user ID {}: username already taken", id);
            throw new IllegalArgumentException("Username taken");
        }
        logger.warn("Username change failed for user ID {}: invalid username", id);
        throw new IllegalArgumentException("Invalid username");
    }

    @Transactional
    public User changeEmail(Long id, String newEmail) {
        if (!SecurityUtils.isAdmin()) {
            // Only allow if the authenticated user is verified and is changing their own
            // email
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if (!principal.isVerified() || !principal.getId().equals(id)) {
                throw new AccessDeniedException("Forbidden");
            }
        }

        User user = findByID(id);
        if (ValidationUtils.isValidEmail(newEmail)) {
            if (!isEmailTaken(newEmail)) {
                user.setEmail(newEmail);
                logger.info("Email changed for user: {} to new email: {}", user.getUsername(), newEmail);
                return userRepository.save(user);
            }
            logger.warn("Email change failed for user ID {}: email already taken", id);
            throw new IllegalArgumentException("Email taken");
        }
        logger.warn("Email change failed for user ID {}: invalid email format", id);
        throw new IllegalArgumentException("Invalid email format");
    }

    @Transactional
    public User changeRole(Long id, UserRole newRole) {
        User user = findByID(id);
        user.setRole(newRole);
        logger.info("Set role {} for user: {}", newRole, user.getUsername());
        return userRepository.save(user);
    }

    // Delete user account
    @Transactional
    public boolean deleteUser(Long id) {
        if (!SecurityUtils.isAdmin()) {
            CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if (!principal.isVerified() || !principal.getId().equals(id)) {
                logger.warn("User ID {} attempted to delete user ID {} without permission", principal.getId(), id);
                throw new AccessDeniedException("Forbidden");
            }
        }
        User user = findByID(id); // Throws if not found
        userRepository.delete(user);
        logger.info("User deleted: {}", user.getUsername());
        return true;
    }

    @Transactional
    // Lock/unlock user account
    public void toggleUserLocked(Long id) {
        User user = findByID(id); // Throws if not found
        user.setLocked(!user.isLocked());
        logger.info("User {} {}", user.isLocked() ? "locked:" : "unlocked:", user.getUsername());
        userRepository.save(user);
    }

    // Check if user or email is already taken
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isUsernameTaken(String username, Long currentUserId) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && !user.get().getId().equals(currentUserId);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isEmailTaken(String email, Long currentUserId) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && !user.get().getId().equals(currentUserId);
    }

    // Handle user verification (TODO)

    // Change / reset password
    @Transactional
    public User changePassword(Long id, String newPassword) {
        User user = findByID(id); // will throw if not found
        if (!ValidationUtils.isValidPassword(newPassword)) {
            logger.warn("Password change failed for user {}: weak password", user.getUsername());
            throw new IllegalArgumentException(
                    "Password must have at least 8 characters, one uppercase letter, one lowercase letter, one digit, and one special character");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        logger.info("Password changed for user: {}", user.getUsername());
        return userRepository.save(user);
    }
}
