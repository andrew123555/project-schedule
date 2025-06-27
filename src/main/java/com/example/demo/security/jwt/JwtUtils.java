package com.example.demo.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import com.example.demo.service.UserDetailsImpl;
import java.security.Key;
import java.util.Date;
import org.springframework.security.core.Authentication;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // 從 application.properties 或 application.yml 注入 JWT 秘密密鑰
    // 這個密鑰必須是 Base64 編碼的字符串，並在生成和驗證 JWT 時保持一致
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // 從 application.properties 或 application.yml 注入 JWT 過期時間 (毫秒)
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 根據 Authentication 物件生成 JWT Token
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    // 生成 JWT Token 從用戶名
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username) // 設定 JWT 的主題 (通常是用戶名)
                .setIssuedAt(new Date()) // 設定 JWT 的簽發時間
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // 設定 JWT 的過期時間
                .signWith(key(), SignatureAlgorithm.HS256) // 使用 HS256 算法和秘密密鑰進行簽名
                .compact(); // 生成最終的 JWT 字符串
    }

    // 生成 HttpOnly ResponseCookie，用於將 JWT 安全地發送給客戶端
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername()); // 使用新的方法生成 Token
        
        // ResponseCookie 配置：
        // name: "jwt" - Cookie 的名稱
        // value: jwt - 生成的 JWT Token
        // path: "/api" - 設定 Cookie 的路徑，通常為 API 的根路徑，確保瀏覽器只在這些路徑下發送 Cookie
        // maxAge: 24 * 60 * 60 - Cookie 的生命週期 (秒)，與 JWT 過期時間一致 (這裡設置為 24 小時)
        // httpOnly: true - 防止客戶端 JavaScript 訪問 Cookie，提高安全性 (防 XSS)
        // secure: false - ⭐ 開發環境下可設為 false (允許 HTTP)，生產環境**務必**設為 true (需要 HTTPS) ⭐
        // sameSite: "Lax" - 設置 SameSite 策略以防止 CSRF 攻擊，推薦使用 Lax 或 Strict
        return ResponseCookie.from("jwt", jwt) 
                .path("/api") 
                .maxAge(24 * 60 * 60) 
                .httpOnly(true)
                .secure(false) 
                .sameSite("Lax") 
                .build();
    }

    // 從 HttpServletRequest 中獲取 JWT Cookie 的值
    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) { // Cookie 名稱要和 generateJwtCookie 中設置的 "jwt" 一致
                    return cookie.getValue();
                }
            }
        }
        return null; // 如果沒有找到 JWT Cookie，返回 null
    }

    // 生成一個清除 JWT Cookie 的 ResponseCookie，用於登出
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from("jwt", "") // 將 Cookie 值設為空字符串
                .path("/api")
                .maxAge(0) // 將最大年齡設置為 0 以強制瀏覽器刪除 Cookie
                .httpOnly(true)
                .secure(false) // 同樣，生產環境為 true
                .sameSite("Lax")
                .build();
    }

    // 從 Base64 編碼的 jwtSecret 生成用於簽名/驗證的 Key
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 從 JWT Token 中提取用戶名 (主題)
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 驗證 JWT Token 的有效性
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true; // Token 有效
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // JWT 格式不正確或簽名無效 (這是您之前遇到的簽名不匹配錯誤)
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // JWT Token 已過期
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 不支持的 JWT Token 格式
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // JWT 的聲明字符串為空
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false; // Token 無效
    }
}