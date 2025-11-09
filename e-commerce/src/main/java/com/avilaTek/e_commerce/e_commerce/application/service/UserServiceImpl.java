package com.avilaTek.e_commerce.e_commerce.application.service;

import com.avilaTek.e_commerce.e_commerce.domain.exception.UserNotFoundException;
import com.avilaTek.e_commerce.e_commerce.domain.model.User;
import com.avilaTek.e_commerce.e_commerce.domain.model.UserRole;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserService;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UsersPage getAllUsers(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all users - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
        validatePaginationParams(page, size);
        List<User> allUsers = userRepository.findAll();
        List<User> sortedUsers = sortUsers(allUsers, sortBy, sortDirection);
        List<User> pagedUsers = applyPagination(sortedUsers, page, size);
        int totalElements = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        log.info("Retrieved {} users out of {} total", pagedUsers.size(), totalElements);
        return new UsersPage(pagedUsers, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional
    public User updateUser(Long id, String name) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User updatedUser = user.updateName(name);
        User savedUser = userRepository.save(updatedUser);

        log.info("User updated successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User deactivatedUser = user.deactivate();
        userRepository.save(deactivatedUser);

        log.info("User deactivated successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User activatedUser = user.activate();
        userRepository.save(activatedUser);

        log.info("User activated successfully with ID: {}", id);
    }

    @Override
    public User createUser(String email, String name, String password, UserRole role) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public User updateUserRole(Long id, UserRole role) {
        return null;
    }

    @Override
    public void changePassword(Long id, String currentPassword, String newPassword) {

    }


    @Override
    public UsersPage searchUsersByEmail(String email, int page, int size, String sortBy, String sortDirection) {
        return null;
    }

    private void validatePaginationParams(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
    }

    private List<User> sortUsers(List<User> users, String sortBy, String sortDirection) {
        Comparator<User> comparator = getComparator(sortBy);

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return users.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<User> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "email" -> Comparator.comparing(User::getEmail);
            case "name" -> Comparator.comparing(User::getName);
            case "createdat" -> Comparator.comparing(User::getCreatedAt);
            case "updatedat" -> Comparator.comparing(User::getUpdatedAt);
            default -> Comparator.comparing(User::getId); // default sort by id
        };
    }

    private List<User> applyPagination(List<User> users, int page, int size) {
        int start = page * size;
        if (start >= users.size()) {
            return List.of();
        }

        int end = Math.min(start + size, users.size());
        return users.subList(start, end);
    }
}
