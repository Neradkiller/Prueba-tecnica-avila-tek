package com.avilaTek.e_commerce.e_commerce.infrastructure.controller;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.application.service.AuthorizationService;
import com.avilaTek.e_commerce.e_commerce.domain.exception.UserNotFoundException;
import com.avilaTek.e_commerce.e_commerce.domain.model.User;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserInfo;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile(
            @RequestHeader("Authorization") String authHeader) {
        try {
            var userInfo = extractAndValidateUserInfo(authHeader);
            Long userId = userInfo.userId();
            log.info("Fetching profile for current user ID: {}", userId);
            return userService.getUserById(userId)
                    .map(user -> {
                        log.info("Successfully retrieved profile for user ID: {}", userId);
                        return ResponseEntity.ok(toResponse(user));
                    })
                    .orElseThrow(() -> {
                        log.error("User not found in database but exists in token. ID: {}", userId);
                        return new UserNotFoundException(userId);
                    });

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for user profile access: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {

        try {
            var userInfo = extractAndValidateUserInfo(authHeader);
            Long userId = userInfo.userId();

            log.info("Updating profile for current user ID: {}", userId);

            var updatedUser = userService.updateUser(userId, request.name());
            log.info("Successfully updated profile for user ID: {}", userId);
            return ResponseEntity.ok(toResponse(updatedUser));

        } catch (AuthorizationException ex) {
            log.warn("Authorization failed for profile update: {}", ex.getMessage());
            throw ex;
        }
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

    public record UpdateProfileRequest(
            @NotBlank(message = "Name is required")
            @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
            String name
    ) {}

    public record UserResponse(Long id, String email, String name, String role, String status,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {}

    private UserInfo extractAndValidateUserInfo(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthorizationException("Missing or invalid authorization header", "INVALID_AUTH_HEADER");
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            throw new AuthorizationException("Invalid or expired token", "INVALID_TOKEN");
        }

        return authService.extractUserInfo(token);
    }
}
