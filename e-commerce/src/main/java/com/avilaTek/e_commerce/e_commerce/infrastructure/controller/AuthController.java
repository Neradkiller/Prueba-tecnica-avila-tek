package com.avilaTek.e_commerce.e_commerce.infrastructure.controller;

import com.avilaTek.e_commerce.e_commerce.domain.model.UserRole;
import com.avilaTek.e_commerce.e_commerce.domain.port.input.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        var result = authService.register(
                request.email(),
                request.name(),
                request.password(),
                UserRole.CLIENT
        );

        var loginResult = authService.login(request.email(), request.password());

        var response = new AuthResponse(
                loginResult.token(),
                result.getId(),
                result.getEmail(),
                result.getName(),
                result.getRole().name()
        );

        return ResponseEntity.created(URI.create("/api/users/" + result.getId()))
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        var result = authService.login(request.email(), request.password());

        var response = new AuthResponse(
                result.token(),
                result.user().getId(),
                result.user().getEmail(),
                result.user().getName(),
                result.user().getRole().name()
        );

        return ResponseEntity.ok(response);
    }

    public record RegisterRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email,

            @NotBlank(message = "Name is required")
            @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
            String name,

            @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password
    ) {}
    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}
    public record AuthResponse(String token, Long userId, String email, String name, String role) {}
}


