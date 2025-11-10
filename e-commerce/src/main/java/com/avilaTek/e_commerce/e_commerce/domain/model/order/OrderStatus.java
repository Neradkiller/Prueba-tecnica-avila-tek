package com.avilaTek.e_commerce.e_commerce.domain.model.order;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED;

    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean isCompleted() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }
}
