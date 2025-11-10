package com.avilaTek.e_commerce.e_commerce.infrastructure.controller.user;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.authorization.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.exception.UserNotFoundException;
import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<UsersPageResponse> getAllUsers(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        try {
            log.info("Fetching all users - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDirection);
            authorizationService.validateAdminAccess(authHeader);

            var usersPage = userService.getAllUsers(page, size, sortBy, sortDirection);
            var response = toUsersPageResponse(usersPage);

            log.info("Successfully retrieved {} users", response.users().size());
            return ResponseEntity.ok(response);

        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user list: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        try {
            log.info("Admin fetching user by ID: {}", id);
            authorizationService.validateAdminAccess(authHeader);

            return userService.getUserById(id)
                    .map(user -> {
                        log.info("Successfully retrieved user ID: {}", id);
                        return ResponseEntity.ok(toResponse(user));
                    })
                    .orElseThrow(() -> new UserNotFoundException(id));

        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user access: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        try {
            log.info("Admin deactivating user ID: {}", id);
            authorizationService.validateAdminAccess(authHeader);

            userService.deactivateUser(id);
            log.info("Successfully deactivated user ID: {}", id);
            return ResponseEntity.noContent().build();

        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user deactivation: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        try {
            log.info("Admin activating user ID: {}", id);
            authorizationService.validateAdminAccess(authHeader);

            userService.activateUser(id);
            log.info("Successfully activated user ID: {}", id);
            return ResponseEntity.noContent().build();

        } catch (AuthorizationException ex) {
            log.warn("Admin authorization failed for user activation: {}", ex.getMessage());
            throw ex;
        }
    }

    private UsersPageResponse toUsersPageResponse(UserService.UsersPage usersPage) {
        var userResponses = usersPage.users().stream()
                .map(this::toUserResponse)
                .toList();

        return new UsersPageResponse(
                userResponses,
                usersPage.page(),
                usersPage.size(),
                usersPage.totalElements(),
                usersPage.totalPages(),
                usersPage.page() > 0,
                usersPage.page() < usersPage.totalPages() - 1
        );
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public record UsersPageResponse(
            List<UserResponse> users,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasPrevious,
            boolean hasNext
    ) {}

    public record UserResponse(
            Long id,
            String email,
            String name,
            String role,
            String status,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt
    ) {}
}
