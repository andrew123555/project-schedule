package com.example.demo.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message = "使用者名稱不能為空")
    @Size(min = 3, max = 20, message = "使用者名稱長度必須在 3 到 20 個字元之間")
    private String username;

    @NotBlank(message = "電子郵件不能為空")
    @Size(max = 50, message = "電子郵件長度不能超過 50 個字元")
    @Email(message = "這不是一個有效的電子郵件格式")
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 40, message = "密碼長度必須在 6 到 40 個字元之間")
    private String password;

    // 無參數建構子 (通常需要，特別是如果使用 Jackson 進行 JSON 反序列化)
    public SignupRequest() {
    }

    // 帶參數的建構子 (可選，但方便測試或在某些情況下創建對象)
    public SignupRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // --- Getters and Setters ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}