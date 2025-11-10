package com.avilaTek.e_commerce.e_commerce.domain.port.input.user;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;
import com.avilaTek.e_commerce.e_commerce.domain.model.user.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User updateUser(Long id, String name);
    void deactivateUser(Long id);
    void activateUser(Long id);
    User createUser(String email, String name, String password, UserRole role);
    void deleteUser(Long id);
    User updateUserRole(Long id, UserRole role);
    void changePassword(Long id, String currentPassword, String newPassword);
    UsersPage getAllUsers(int page, int size, String sortBy, String sortDirection);
    UsersPage searchUsersByEmail(String email, int page, int size, String sortBy, String sortDirection);

    record UsersPage(List<User> users, int page, int size, long totalElements, int totalPages) {}
}
