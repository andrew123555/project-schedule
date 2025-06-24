package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handle(Throwable ex) {
        ex.printStackTrace(); // 強制印出錯誤堆疊
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("💥 系統錯誤：" + ex.getMessage());
    }
}