package com.avilaTek.e_commerce.e_commerce.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}
