// src/main/java/com/example/demo/exception/handler/GlobalExceptionHandler.java
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // 導入 Spring Security 的 AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "您沒有足夠的權限執行此操作。所需權限：MODERATOR 或 ADMIN。"); 
        body.put("path", request.getDescription(false).replace("uri=", "")); 

        System.err.println("Access Denied: " + ex.getMessage() + " for path: " + request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN); 
    }

    @ExceptionHandler(com.example.demo.exception.ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            com.example.demo.exception.ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND); // 返回 404 Not Found
    }

    // 處理其他未被特定處理的通用例外
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "發生了一個未預期的錯誤：" + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        ex.printStackTrace(); // 在開發環境中可以打印堆棧跟踪
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR); // 返回 500 Internal Server Error
    }
}