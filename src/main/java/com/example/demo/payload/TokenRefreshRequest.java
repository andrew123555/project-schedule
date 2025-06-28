package com.example.demo.payload;

import jakarta.validation.constraints.NotBlank;

//刷新令牌請求的數據傳輸對象 (DTO)
public class TokenRefreshRequest {
 @NotBlank // 確保刷新令牌不能為空
 private String refreshToken;

 // 無參數構造函數
 public TokenRefreshRequest() {
 }

 // 帶參數構造函數
 public TokenRefreshRequest(String refreshToken) {
     this.refreshToken = refreshToken;
 }

 // Getter 方法
 public String getRefreshToken() {
     return refreshToken;
 }

 // Setter 方法
 public void setRefreshToken(String refreshToken) {
     this.refreshToken = refreshToken;
 }
}
