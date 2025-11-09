package com.avilaTek.e_commerce.e_commerce.domain.port.input;

import com.avilaTek.e_commerce.e_commerce.domain.model.User;

public record AuthenticationResult(String token, User user) {
}
