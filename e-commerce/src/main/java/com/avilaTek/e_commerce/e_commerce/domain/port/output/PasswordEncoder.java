package com.avilaTek.e_commerce.e_commerce.domain.port.output;

public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
