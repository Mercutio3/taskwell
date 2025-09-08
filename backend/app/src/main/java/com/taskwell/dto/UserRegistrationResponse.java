package com.taskwell.dto;

public class UserRegistrationResponse {
    private Long id;
    private String username;
    private String email;
    private boolean locked;
    private boolean verified;
    private String token;

    public UserRegistrationResponse(Long id, String username, String email, boolean locked,
            boolean verified, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.locked = locked;
        this.verified = verified;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
