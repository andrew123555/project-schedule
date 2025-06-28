package com.example.demo.service;

import com.example.demo.exception.TokenRefreshException;
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.model.entity.User;
import com.example.demo.repository.RefreshTokenRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // 導入 @Value
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant; // 導入 Instant
import java.util.Optional;
import java.util.UUID; // 導入 UUID

@Service
@Transactional // 確保所有操作都在事務中執行
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}") // 從 application.properties 讀取刷新令牌過期時間 (毫秒)
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 根據用戶 ID 創建或更新刷新令牌。
     * 如果用戶已有刷新令牌，則更新它；否則創建一個新的。
     * @param userId 用戶的 ID
     * @return 新創建或更新的 RefreshToken 物件
     */
    public RefreshToken createRefreshToken(Long userId) {
        // 嘗試查找該用戶現有的刷新令牌
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
        RefreshToken refreshToken;

        if (existingToken.isPresent()) {
            // 如果存在，則更新現有的令牌
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString()); // 生成新的隨機令牌字串
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs)); // 更新過期時間
        } else {
            // 如果不存在，則創建一個新的刷新令牌
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(UUID.randomUUID().toString()); // 生成隨機令牌字串
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs)); // 設定過期時間
        }

        return refreshTokenRepository.save(refreshToken); // 保存到資料庫
    }

    /**
     * 根據令牌字串查找刷新令牌。
     * @param token 刷新令牌字串
     * @return 包含 RefreshToken 物件的 Optional
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * 驗證刷新令牌是否過期。
     * @param token 刷新令牌物件
     * @return 如果未過期，返回相同的 RefreshToken 物件
     * @throws TokenRefreshException 如果令牌已過期
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) { // 比較過期時間與當前時間
            refreshTokenRepository.delete(token); // 如果過期，則從資料庫中刪除
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    /**
     * 根據用戶 ID 刪除所有相關的刷新令牌。
     * @param userId 用戶的 ID
     * @return 刪除的行數
     */
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
