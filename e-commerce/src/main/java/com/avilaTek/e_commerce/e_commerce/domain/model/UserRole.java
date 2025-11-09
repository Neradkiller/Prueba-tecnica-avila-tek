package com.avilaTek.e_commerce.e_commerce.domain.model;

public enum UserRole {
    ADMIN, CLIENT;

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean canAccessAdminFeatures() {
        return this == ADMIN;
    }
}
