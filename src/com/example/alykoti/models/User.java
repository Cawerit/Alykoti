package com.example.alykoti.models;

import com.example.alykoti.services.AuthService;

public class User {
    private String username;
    private AuthService.Role role;

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
