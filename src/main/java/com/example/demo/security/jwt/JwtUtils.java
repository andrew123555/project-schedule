package com.example.demo.security.jwt;

import com.example.demo.service.UserDetailsImpl;
import io.jsonwebtoken.*; // 導入所有 io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders; // 導入 Decoders
import io.jsonwebtoken.security.Keys; // 導入 Keys
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication; // 導入 Authentication
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}") // 從 application.properties 讀取 JWT 密鑰
    private String jwtSecret;

    @Value("${jwt.expirationMs}") // 從 application.properties 讀取 JWT 過期時間 (毫秒)
    private int jwtExpirationMs;

    // 從 JWT 密鑰字串生成用於簽名的 Key
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // ⭐ 修正點：接收 Authentication 物件來生成 JWT ⭐
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // 設定主題為用戶名
                .setIssuedAt(new Date()) // 設定發行時間
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // 設定過期時間
                .signWith(key(), SignatureAlgorithm.HS512) // 使用 HS512 算法和密鑰簽名
                .compact(); // 壓縮成 JWT 字串
    }

    // 根據用戶名生成 JWT (用於刷新令牌時，可能只有用戶名)
    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 從 JWT 字串獲取用戶名
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 驗證 JWT 令牌
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}