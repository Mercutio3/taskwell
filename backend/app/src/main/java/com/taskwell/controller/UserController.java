package com.taskwell.controller;

import com.taskwell.dto.ChangePasswordRequest;
import com.taskwell.dto.UserRegistrationResponse;
import com.taskwell.dto.UserRegistrationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import com.taskwell.service.UserService;
import com.taskwell.model.User;
import com.taskwell.dto.ChangeRoleRequest;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@Tag(name = "User API", description = "Operations related to users.")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user and returns it.")
    @ApiResponse(responseCode = "201", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid or taken input data.")
    @PostMapping("/api/users")
    public ResponseEntity<UserRegistrationResponse> createUser(
            @Valid @RequestBody UserRegistrationRequest userRequest) {
        User createdUser = userService.registerUserFromDto(userRequest);
        UserRegistrationResponse response = new UserRegistrationResponse(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.isLocked(),
                createdUser.isVerified(),
                createdUser.getVerificationToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Verify user account", description = "Verifies a user account using a token.")
    @ApiResponse(responseCode = "200", description = "User verified successfully")
    @ApiResponse(responseCode = "400", description = "Invalid verification token")
    @GetMapping("/api/users/verify")
    public ResponseEntity<Void> verifyUser(@RequestParam("token") String token) {
        boolean verified = userService.verifyUser(token);
        if (verified) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of users returned successfully")
    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Returns a single user by its ID.")
    @ApiResponse(responseCode = "200", description = "User found.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/api/users/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        User user = userService.findByID(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by username", description = "Returns a single user by its username.")
    @ApiResponse(responseCode = "200", description = "User found.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/api/users/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve") @PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by email", description = "Returns a single user by its email.")
    @ApiResponse(responseCode = "200", description = "User found.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/api/users/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email of the user to retrieve") @PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update a user's username", description = "Updates an existing user's username and returns it.")
    @ApiResponse(responseCode = "200", description = "User updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid or taken username.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/api/users/{id}/username")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "User object containing new username") @Valid @RequestBody User user) {
        User updatedUser = userService.changeUsername(id, user.getUsername());
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Change a user's email", description = "Changes an existing user's email and returns it.")
    @ApiResponse(responseCode = "200", description = "User updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid or taken email.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/api/users/{id}/email")
    public ResponseEntity<User> updateEmail(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "User object containing new email") @Valid @RequestBody User user) {
        User updatedUser = userService.changeEmail(id, user.getEmail());
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Change a user's role", description = "Changes an existing user's role and returns it.")
    @ApiResponse(responseCode = "200", description = "User updated successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid role.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/api/users/{id}/role")
    public ResponseEntity<User> updateRole(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "Role change request") @Valid @RequestBody ChangeRoleRequest request) {
        User updatedUser = userService.changeRole(id, request.getRole());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by its ID.")
    @ApiResponse(responseCode = "204", description = "User deleted successfully.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Toggle user account lock", description = "Toggles a user account lock status by its ID.")
    @ApiResponse(responseCode = "200", description = "User account lock status toggled successfully.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/api/users/{id}/toggle-lock")
    public ResponseEntity<Void> toggleUserLock(
            @Parameter(description = "ID of the user to toggle lock status") @PathVariable Long id) {
        try {
            userService.toggleUserLocked(id);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Change user password", description = "Changes an existing user's password.")
    @ApiResponse(responseCode = "200", description = "User password changed successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid/weak password.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/api/users/{id}/password")
    public ResponseEntity<User> changeUserPassword(
            @Parameter(description = "ID of the user to change password") @PathVariable Long id,
            @Parameter(description = "New password") @Valid @RequestBody ChangePasswordRequest request) {
        User updatedUser = userService.changePassword(id, request.getPassword());
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedUser);
    }
}