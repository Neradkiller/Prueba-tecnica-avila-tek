package com.avilaTek.e_commerce.e_commerce.application.service;

import com.avilaTek.e_commerce.e_commerce.domain.exception.DomainException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.InvalidCredentialsException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.InvalidUserStatusException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.UserAlreadyExistsException;
import com.avilaTek.e_commerce.e_commerce.domain.model.User;
import com.avilaTek.e_commerce.e_commerce.domain.model.UserRole;
import com.avilaTek.e_commerce.e_commerce.domain.model.UserStatus;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthenticationResult;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.UserInfo;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.PasswordEncoder;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.TokenProvider;
import com.avilaTek.e_commerce.e_commerce.domain.port.output.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    @Transactional
    public User register(String email, String name, String password, UserRole role) {
        log.info("Registering new user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        if (password == null || password.length() < 6) {
            throw new DomainException("Password must be at least 6 characters long");
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .name(name)
                .passwordHash(passwordHash)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResult login(String email, String password) {
        log.info("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.canLogin()) {
            throw new InvalidUserStatusException("User account is not active");
        }

        String token = tokenProvider.generateToken(user);
        log.info("User logged in successfully with ID: {}", user.getId());

        return new AuthenticationResult(token, user);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }

    @Override
    public UserInfo extractUserInfo(String token) {
        return tokenProvider.extractUserInfo(token);
    }
}
