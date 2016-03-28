package com.example.alykoti;

import com.example.alykoti.services.AuthService;

/**
 * Etusivu adminille
 */
public class AdminDashboardView extends AppView {
    public AdminDashboardView() {
        super(AuthService.Role.ADMIN);
    }
}
