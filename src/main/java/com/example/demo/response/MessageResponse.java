package com.example.demo.response;

public class MessageResponse {
    private String message;

    // 無參數構造函數
    public MessageResponse() {
    }

    // 帶參數構造函數
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter 方法
    public String getMessage() {
        return message;
    }

    // Setter 方法
    public void setMessage(String message) {
        this.message = message;
    }
}
