package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 自定義異常，當刷新令牌無效或過期時拋出
@ResponseStatus(HttpStatus.FORBIDDEN) // 返回 HTTP 403 Forbidden 狀態碼
public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // 構造函數，接受刷新令牌和錯誤訊息
    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
