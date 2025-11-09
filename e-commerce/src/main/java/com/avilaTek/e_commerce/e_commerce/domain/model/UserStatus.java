package com.avilaTek.e_commerce.e_commerce.domain.model;

public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED;

    public boolean canLogin() {
        return this == ACTIVE;
    }
}
