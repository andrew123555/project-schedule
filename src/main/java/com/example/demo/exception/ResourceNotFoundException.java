package com.example.demo.exception;

import org.springframework.http.HttpStatus; // 導入 HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus; // 導入 ResponseStatus 註解

/**
 * 自定義的資源未找到例外。
 * 當資源（例如，資料庫中的實體）透過給定的 ID 或其他標識符無法找到時拋出。
 * 結合 @ResponseStatus(HttpStatus.NOT_FOUND) 註解，
 * 當此例外被控制器方法拋出時，Spring 會自動將 HTTP 狀態碼設置為 404 Not Found。
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 這會將 HTTP 狀態碼設置為 404
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L; // 建議添加 serialVersionUID

    /**
     * 構造一個新的 ResourceNotFoundException，帶有指定的詳細訊息。
     *
     * @param message 例外訊息。
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 構造一個新的 ResourceNotFoundException，帶有指定的詳細訊息和原因。
     *
     * @param message 例外訊息。
     * @param cause   此例外的根本原因。
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}