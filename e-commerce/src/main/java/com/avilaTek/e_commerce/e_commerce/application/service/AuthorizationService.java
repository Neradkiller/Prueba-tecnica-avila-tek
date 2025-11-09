package com.avilaTek.e_commerce.e_commerce.application.service;

import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final AuthService authService;

    public void validateAdminAccess(String authHeader) {
        UserInfo userInfo = extractAndValidateUser(authHeader);
        if (!userInfo.role().canAccessAdminFeatures()) {
            throw new AuthorizationException("Admin access required", "ADMIN_ACCESS_REQUIRED");
        }
    }

    public void validateUserAccess(String authHeader, Long requestedUserId) {
        UserInfo userInfo = extractAndValidateUser(authHeader);

        if (!userInfo.userId().equals(requestedUserId) && !userInfo.role().isAdmin()) {
            throw new AuthorizationException("Access denied to user data", "ACCESS_DENIED");
        }
    }

    private UserInfo extractAndValidateUser(String authHeader) {
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
