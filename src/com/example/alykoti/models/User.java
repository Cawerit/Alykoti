package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;

public class User {
    private final String username;
    private final AuthService.Role role;

    public User(String username, AuthService.Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public AuthService.Role getRole() {
        return role;
    }
}
