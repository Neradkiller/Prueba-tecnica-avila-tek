package com.avilaTek.e_commerce.e_commerce.application.exception;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {
    private final String errorCode;

    public AuthorizationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthorizationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
