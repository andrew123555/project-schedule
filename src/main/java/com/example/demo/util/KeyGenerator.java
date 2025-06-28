package com.example.demo.util;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        // 為 HS512 生成一個安全的密鑰
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated HS512 Key (Base64 encoded): " + base64Key);
        System.out.println("Key Length (Base64): " + base64Key.length());
        System.out.println("Key Length (Bytes): " + keyBytes.length);
    }
}