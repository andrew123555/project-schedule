package com.example.demo.repository;

import com.example.demo.model.entity.PasswordResetToken;
import com.example.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user); // 新增：根據用戶刪除所有相關的 Token
    Optional<PasswordResetToken> findByUser(User user); // 根據用戶查找密碼重設 Token

}