package com.lumicare.weerobot.domain.emotion.exception;

public class IncorrectLoginException extends RuntimeException {
    public IncorrectLoginException(String message) {
        super(message);
    }
}