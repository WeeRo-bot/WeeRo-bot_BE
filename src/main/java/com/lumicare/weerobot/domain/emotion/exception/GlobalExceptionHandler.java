package com.lumicare.weerobot.domain.emotion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 아이디 중복
    @ExceptionHandler(DuplicateIDException.class)
    public ResponseEntity<String> handleDuplicateID(DuplicateIDException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT) // 409
                .body(ex.getMessage());
    }

    // 로그인 실패 (아이디, 비밀번호 틀림)
    @ExceptionHandler(IncorrectLoginException.class)
    public ResponseEntity<String> handleIncorrectLogin(IncorrectLoginException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                .body(ex.getMessage());
    }

    // 비밀번호 조건 불일치
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                .body(ex.getMessage());
    }

    // 기타 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                .body("서버 오류가 발생했습니다.");
    }
}
