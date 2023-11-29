package com.neobis.authproject.exception;

public class IncorrectLoginException extends RuntimeException{
    public IncorrectLoginException(String message) {
        super(message);
    }
}
