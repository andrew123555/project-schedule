package com.example.demo.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.example.demo.service.UserDetailsImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${demo.app.jwtSecret}") // 從 application.properties 或 application.yml 讀取 JWT 密鑰
    private String jwtSecret;

    @Value("${demo.app.jwtExpirationMs}") // 從 application.properties 或 application.yml 讀取 JWT 過期時間 (毫秒)
    private int jwtExpirationMs;

    @Value("${demo.app.jwtCookieName}") // 從 application.properties 或 application.yml 讀取 Cookie 名稱
    private String jwtCookieName;

    // 獲取 JWT 簽名密鑰
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 從 HttpServletRequest 中獲取 JWT Cookie 的值
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // ⭐ 新增或修改 generateJwtCookie 方法
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        return ResponseCookie.from(jwtCookieName, jwt)
                .path("/api") // 通常將 JWT Cookie 的路徑設定為 /api 或更具體的路徑
                .maxAge(24 * 60 * 60) // Cookie 有效期，這裡設定為 24 小時
                .httpOnly(true) // 防止客戶端腳本訪問 Cookie
                .build();
    }

    // ⭐ 新增或修改 getCleanJwtCookie 方法 (用於登出時清除 Cookie)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookieName, "")
                .path("/api") // 路徑必須與生成時的路徑匹配，否則無法清除
                .maxAge(0) // 將最大生存時間設定為 0 以刪除 Cookie
                .httpOnly(true)
                .build();
    }

    // 從用戶名生成 JWT Token
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 驗證 JWT Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("無效的 JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token 已過期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("不支援的 JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims 字符串為空: {}", e.getMessage());
        }
        return false;
    }

    // 從 JWT Token 中獲取用戶名
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }
}