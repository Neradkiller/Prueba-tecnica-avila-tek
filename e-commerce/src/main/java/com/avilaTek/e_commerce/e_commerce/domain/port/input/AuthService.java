package com.avilaTek.e_commerce.e_commerce.domain.port.input;

import com.avilaTek.e_commerce.e_commerce.domain.model.User;
import com.avilaTek.e_commerce.e_commerce.domain.model.UserRole;

public interface AuthService {
    User register(String email, String name, String password, UserRole role);
    AuthenticationResult login(String email, String password);
    boolean validateToken(String token);
    UserInfo extractUserInfo(String token);
}

