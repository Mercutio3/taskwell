package com.taskwell.dto;

import com.taskwell.model.UserRole;
import jakarta.validation.constraints.NotNull;

public class ChangeRoleRequest {
    @NotNull(message = "Role is required")
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}