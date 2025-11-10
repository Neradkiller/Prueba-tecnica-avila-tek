package com.avilaTek.e_commerce.e_commerce.domain.port.output;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.user.UserInfo;

public interface TokenProvider {
    String generateToken(User user);
    boolean validateToken(String token);
    UserInfo extractUserInfo(String token);
}
