package com.example.demo.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;

    @NotBlank // ⭐ 新增：確認密碼字段 ⭐
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    // ⭐ 修正：將 voidNewPassword 改為 setNewPassword ⭐
    public void setNewPassword(String newPassword) { 
        this.newPassword = newPassword;
    }

    // ⭐ 新增：confirmPassword 的 getter 和 setter ⭐
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}