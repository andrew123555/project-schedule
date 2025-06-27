package com.example.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        System.out.println("--- 生成 10 個測試用戶的加密密碼 ---");
        for (int i = 1; i <= 10; i++) {
            String rawPassword = "testuser" + i + "password"; // 明文密碼範例：testuser1password, testuser2password...
            String encodedPassword = passwordEncoder.encode(rawPassword);
            System.out.println("用戶 testuser" + i + " 的加密密碼是: " + encodedPassword);
        }
        System.out.println("------------------------------------");
    }
}
