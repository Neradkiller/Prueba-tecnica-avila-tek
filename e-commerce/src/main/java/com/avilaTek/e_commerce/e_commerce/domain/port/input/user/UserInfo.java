package com.avilaTek.e_commerce.e_commerce.domain.port.input.user;

import com.avilaTek.e_commerce.e_commerce.domain.model.user.UserRole;

public record UserInfo(Long userId, String email, UserRole role) {
}
