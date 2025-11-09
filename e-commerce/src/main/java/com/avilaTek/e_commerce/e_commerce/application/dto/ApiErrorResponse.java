package com.avilaTek.e_commerce.e_commerce.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String errorCode;
    private final Map<String, String> details;
    private final List<ValidationError> validationErrors;

    @Getter
    @Builder
    public static class ValidationError {
        private final String field;
        private final String message;
        private final String rejectedValue;
    }

    public static ApiErrorResponse validationError(
            String message,
            List<ValidationError> validationErrors,
            String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .message(message)
                .path(path)
                .errorCode("VALIDATION_ERROR")
                .validationErrors(validationErrors)
                .build();
    }

    public static ApiErrorResponse businessError(
            String message,
            String errorCode,
            String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(422) // Unprocessable Entity
                .error("Business Rule Violation")
                .message(message)
                .path(path)
                .errorCode(errorCode)
                .build();
    }

    public static ApiErrorResponse notFound(
            String message,
            String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("Not Found")
                .message(message)
                .path(path)
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    public static ApiErrorResponse unauthorized(
            String message,
            String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message(message)
                .path(path)
                .errorCode("UNAUTHORIZED")
                .build();
    }

    public static ApiErrorResponse internalError(
            String message,
            String path) {
        return ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .message(message)
                .path(path)
                .errorCode("INTERNAL_ERROR")
                .build();
    }

}
