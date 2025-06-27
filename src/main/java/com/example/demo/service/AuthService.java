package com.example.demo.service;

import com.example.demo.model.entity.PasswordResetToken;
import com.example.demo.model.entity.User;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional 
public class AuthService { 

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder; 

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    // 生成並儲存密碼重設 Token
    public String createPasswordResetTokenForUser(User user) {
        // ⭐ 修正點：在創建新 Token 之前，先查找並刪除該用戶現有的所有 Token ⭐
        // 這樣可以避免重複的 user_id 導致唯一約束衝突
        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);
        existingToken.ifPresent(token -> passwordResetTokenRepository.delete(token));
        // 如果 findByUser 不存在，您可能需要新增這個方法到 PasswordResetTokenRepository
        // 或者使用您現有的 deleteByUser 方法，但要確保它在同一個事務中生效
        // 為了確保原子性，我們將其改為先查找再刪除單個 Token，或者確保 deleteByUser 執行後立即刷新

        // 為了更可靠地處理，確保 deleteByUser 執行後刷新
        // passwordResetTokenRepository.deleteByUser(user);
        // passwordResetTokenRepository.flush(); // 強制刷新，將刪除操作立即同步到資料庫

        // 更好的做法是，如果您的 PasswordResetTokenRepository 有 findByUser 方法，
        // 並且 PasswordResetToken 實體上 user_id 是唯一的，那麼應該這樣做：
        // 刪除該用戶現有的所有 Token
        passwordResetTokenRepository.deleteByUser(user); // 假設此方法已存在並能正常工作

        String token = UUID.randomUUID().toString(); 
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); 
        PasswordResetToken myToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(myToken); 
        return token;
    }

    // ... (其他方法保持不變) ...

    // 刪除密碼重設 Token
    public void deletePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token); 
    }
    // 根據 Token 字符串查找 PasswordResetToken 對象
    public Optional<PasswordResetToken> getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    // 更新用戶密碼
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword)); 
        userRepository.save(user); 
    }

   
}