package com.avilaTek.e_commerce.e_commerce.domain.model.user;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "UserBuilder", toBuilder = true)
public class User {
    private final Long id;
    private final String email;
    private final String name;
    private final String passwordHash;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private User(Long id, String email, String name, String passwordHash,
                 UserRole role, UserStatus status, LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validate();
    }

    private void validate() {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new DomainException("Invalid email format");
        }
        if (name == null || name.isBlank() || name.length() < 2) {
            throw new DomainException("Name must be at least 2 characters long");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainException("Password hash cannot be blank");
        }
    }

    public User deactivate() {
        return this.toBuilder()
                .status(UserStatus.INACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User activate() {
        return this.toBuilder()
                .status(UserStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public User updateName(String newName) {
        return this.toBuilder()
                .name(newName)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public boolean hasRole(UserRole requiredRole) {
        return this.role == requiredRole;
    }

    public boolean canAccessAdminFeatures() {
        return this.role.canAccessAdminFeatures();
    }

    public boolean canLogin() {
        return this.status.canLogin();
    }

}
