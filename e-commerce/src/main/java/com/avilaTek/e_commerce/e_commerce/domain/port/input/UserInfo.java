package com.avilaTek.e_commerce.e_commerce.domain.port.input;

import com.avilaTek.e_commerce.e_commerce.domain.model.UserRole;

public record UserInfo(Long userId, String email, UserRole role) {
}
