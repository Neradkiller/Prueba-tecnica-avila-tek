package com.avilaTek.e_commerce.e_commerce.domain.port.input.auth;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.User;

public record AuthenticationResult(String token, User user) {
}
