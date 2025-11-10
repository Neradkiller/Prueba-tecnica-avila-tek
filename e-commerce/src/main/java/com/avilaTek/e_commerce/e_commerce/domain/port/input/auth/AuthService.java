package com.avilaTek.e_commerce.e_commerce.domain.port.input.auth;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;
import com.avilaTek.e_commerce.e_commerce.domain.model.user.UserRole;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserInfo;

public interface AuthService {
    User register(String email, String name, String password, UserRole role);
    AuthenticationResult login(String email, String password);
    boolean validateToken(String token);
    UserInfo extractUserInfo(String token);
}

