package com.avilaTek.e_commerce.e_commerce.infrastructure.controller;

import com.avilaTek.e_commerce.e_commerce.application.dto.ApiErrorResponse;
import com.avilaTek.e_commerce.e_commerce.application.exception.AuthorizationException;
import com.avilaTek.e_commerce.e_commerce.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ApiErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationError)
                .collect(Collectors.toList());

        ApiErrorResponse errorResponse = ApiErrorResponse.validationError(
                "Validation failed for one or more fields",
                validationErrors,
                getPath(request)
        );

        log.warn("Validation error: {}", validationErrors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private ApiErrorResponse.ValidationError mapToValidationError(FieldError fieldError) {
        return ApiErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue() != null ?
                        fieldError.getRejectedValue().toString() : null)
                .build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.businessError(
                ex.getMessage(),
                "USER_ALREADY_EXISTS",
                getPath(request)
        );

        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.businessError(
                ex.getMessage(),
                "INVALID_CREDENTIALS",
                getPath(request)
        );

        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.notFound(
                ex.getMessage(),
                getPath(request)
        );

        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidUserStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidUserStatus(
            InvalidUserStatusException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.businessError(
                ex.getMessage(),
                "INVALID_USER_STATUS",
                getPath(request)
        );

        log.warn("Invalid user status: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthorizationException(
            AuthorizationException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.unauthorized(
                ex.getMessage(),
                getPath(request)
        );

        log.warn("Authorization failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(
            DomainException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.businessError(
                ex.getMessage(),
                "DOMAIN_RULE_VIOLATION",
                getPath(request)
        );

        log.warn("Domain rule violation: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        log.error("Internal server error: {}", ex.getMessage(), ex);

        ApiErrorResponse errorResponse = ApiErrorResponse.internalError(
                "An unexpected error occurred. Please try again later.",
                getPath(request)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.businessError(
                ex.getMessage(),
                "INVALID_INPUT",
                getPath(request)
        );

        log.warn("Invalid input: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
