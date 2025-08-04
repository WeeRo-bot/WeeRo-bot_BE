package com.lumicare.weerobot.domain.emotion.exception;

public class DuplicateIDException extends RuntimeException {
    public DuplicateIDException(String message) {
        super(message);
    }
}