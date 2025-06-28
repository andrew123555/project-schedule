package com.example.demo.repository;

import com.example.demo.model.entity.RefreshToken;
import com.example.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 根據令牌字串查找刷新令牌
    Optional<RefreshToken> findByToken(String token);

    // 根據用戶 ID 查找刷新令牌 (如果一個用戶只有一個刷新令牌)
    Optional<RefreshToken> findByUserId(Long userId);

    // 根據用戶物件刪除刷新令牌
    @Modifying // 表示此方法會修改資料庫
    int deleteByUser(User user);
}
