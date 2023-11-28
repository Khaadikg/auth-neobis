package com.neobis.authproject.exception;

public class RegistrationTokenExpiredException extends RuntimeException{
    public RegistrationTokenExpiredException(String message) {
        super(message);
    }

}
