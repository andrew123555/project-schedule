package com.example.demo.model.entity;

import jakarta.persistence.*;
import java.time.Instant; // 導入 Instant

// 刷新令牌實體，用於儲存和管理用戶的刷新令牌
@Entity
@Table(name = "refresh_tokens") // 定義資料庫表名
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 與 User 實體的 Many-to-One 關聯
    // 一個用戶可以有多個刷新令牌，一個刷新令牌只屬於一個用戶
    @OneToOne // 這裡使用 OneToOne，通常一個用戶只會有一個有效的刷新令牌用於刷新
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true) // 刷新令牌本身是唯一的
    private String token;

    @Column(nullable = false)
    private Instant expiryDate; // 令牌的過期時間，使用 Instant 類型

    // 無參數構造函數 (JPA 需要)
    public RefreshToken() {
    }

    // 帶參數構造函數
    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // --- Getters and Setters ---
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }
}
