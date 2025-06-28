package com.example.demo.service;

import com.example.demo.exception.TokenRefreshException;
import com.example.demo.exception.ResourceNotFoundException; // 導入 ResourceNotFoundException
import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.PasswordResetToken; // 導入 PasswordResetToken
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.UserActivity.ActionType;
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.SignupRequest;
import com.example.demo.payload.TokenRefreshRequest;
import com.example.demo.response.TokenRefreshResponse;
import com.example.demo.repository.PasswordResetTokenRepository; // 導入 PasswordResetTokenRepository
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID; // 導入 UUID
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    UserActivityService userActivityService;

    @Autowired
    EmailService emailService; // ⭐ 新增：注入 EmailService ⭐

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository; // ⭐ 新增：注入 PasswordResetTokenRepository ⭐

    public JwtResponse authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            userActivityService.recordActivity(ActionType.login_success,
                                               "用戶登入成功",
                                               "用戶: " + userDetails.getUsername(),
                                               request.getRemoteAddr());

            return new JwtResponse(jwt, refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);

        } catch (Exception e) {
            userActivityService.recordActivity(ActionType.login_failure,
                                               "用戶登入失敗",
                                               "用戶: " + loginRequest.getUsername() + ", 錯誤: " + e.getMessage(),
                                               request.getRemoteAddr());
            throw e;
        }
    }

    public User registerUser(SignupRequest signUpRequest, HttpServletRequest request) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            userActivityService.recordActivity(ActionType.register_success,
                                               "用戶註冊失敗",
                                               "用戶名已存在: " + signUpRequest.getUsername(),
                                               request.getRemoteAddr());
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            userActivityService.recordActivity(ActionType.register_success,
                                               "用戶註冊失敗",
                                               "Email 已存在: " + signUpRequest.getEmail(),
                                               request.getRemoteAddr());
            throw new RuntimeException("Error: Email is already in use!");
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        userActivityService.recordActivity(ActionType.register_success,
                                           "新用戶註冊成功",
                                           "新用戶: " + savedUser.getUsername() + ", 角色: " + roles.stream().map(r -> r.getName().name()).collect(Collectors.joining(", ")),
                                           request.getRemoteAddr());
        return savedUser;
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    userActivityService.recordActivity(ActionType.api_access,
                                                       "刷新訪問令牌",
                                                       "用戶: " + user.getUsername(),
                                                       "unknown");
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    public void logout(Long userId, HttpServletRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        String username = userOptional.map(User::getUsername).orElse("未知用戶");
        userActivityService.recordActivity(ActionType.logout,
                                           "用戶登出",
                                           "用戶登出成功",
                                           request.getRemoteAddr());
        refreshTokenService.deleteByUserId(userId);
    }

    // ⭐ 新增：處理忘記密碼請求的業務邏輯 ⭐
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // 刪除用戶所有現有的重設令牌，確保只有一個有效令牌
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        // 令牌有效期設置為 24 小時
        Instant expiryDate = Instant.now().plusSeconds(24 * 3600); 
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    // ⭐ 新增：處理重設密碼的業務邏輯 ⭐
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token."));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken); // 刪除過期令牌
            throw new ResourceNotFoundException("Password reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken); // 成功重設後刪除令牌
    }
}