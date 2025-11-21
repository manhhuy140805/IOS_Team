package com.manhhuy.myapplication.utils;

public class MockUserManager {
    public enum Role {
        CUSTOMER,
        ADMIN
    }

    private static Role currentRole = Role.CUSTOMER; // Default role

    public static void setRole(Role role) {
        currentRole = role;
    }

    public static Role getCurrentRole() {
        return currentRole;
    }

    public static boolean isAdmin() {
        return currentRole == Role.ADMIN;
    }
}
