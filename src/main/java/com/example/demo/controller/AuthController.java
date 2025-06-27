package com.example.demo.controller;

import com.example.demo.payload.ForgotPasswordRequest; // 導入新的請求 DTO
import com.example.demo.payload.ResetPasswordRequest; // 導入新的請求 DTO
import com.example.demo.service.EmailService; // 導入 EmailService
import com.example.demo.service.AuthService; // 導入 AuthService (包含密碼重設邏輯)
import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.PasswordResetToken;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.RegisterRequest;
import com.example.demo.payload.SignupRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.response.MessageResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value; // 導入 Value 註解
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {
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
    EmailService emailService; // 注入 EmailService

    @Autowired
    AuthService authService; // 注入 AuthService (包含密碼重設邏輯)

    @Value("${frontend.url}") // ⭐ 從 application.properties 讀取前端 URL ⭐
    private String frontendUrl;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails); // 這裡需要您實現生成 Cookie 的方法

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 返回用戶信息和 Token (這裡通過 HTTP 響應頭或響應體傳遞)
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString()) // 如果您使用 Cookie
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles,
                        jwtCookie.getValue() // 這裡將 token 返回給前端，以便前端可以儲存
                ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
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
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    // ⭐ 新增：忘記密碼請求端點 ⭐
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(forgotPasswordRequest.getEmail());

        if (!userOptional.isPresent()) {
            // 為了安全，即使郵箱不存在也返回成功訊息，避免洩露用戶信息
            return ResponseEntity.ok(new MessageResponse("如果郵箱存在，密碼重設連結已發送。"));
        }

        User user = userOptional.get();
        String token = authService.createPasswordResetTokenForUser(user); // 生成並儲存 Token

        // 構建密碼重設連結
        String resetLink = frontendUrl + "/reset-password?token=" + token; // ⭐ 這裡需要前端的重設密碼路由 ⭐

        // 發送郵件
        emailService.sendEmail(
                user.getEmail(),
                "密碼重設請求",
                "您好 " + user.getUsername() + ",\n\n" +
                        "我們收到了您的密碼重設請求。請點擊以下連結重設您的密碼：\n" +
                        resetLink + "\n\n" +
                        "如果這不是您發起的請求，請忽略此郵件。\n\n" +
                        "此連結將在 24 小時後過期。\n\n" +
                        "此致,\n您的應用程式團隊"
        );

        return ResponseEntity.ok(new MessageResponse("如果郵箱存在，密碼重設連結已發送。"));
    }

    // ⭐ 新增：重設密碼端點 ⭐
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(new MessageResponse("新密碼和確認密碼不匹配！"));
        }

        Optional<PasswordResetToken> tokenOptional = authService.getPasswordResetToken(token);

        if (!tokenOptional.isPresent() || tokenOptional.get().isExpired()) {
            return ResponseEntity.badRequest().body(new MessageResponse("無效或已過期的密碼重設連結。"));
        }

        PasswordResetToken resetToken = tokenOptional.get();
        User user = resetToken.getUser();

        authService.changeUserPassword(user, newPassword); // 更新用戶密碼
        authService.deletePasswordResetToken(resetToken); // 刪除已使用的 Token

        return ResponseEntity.ok(new MessageResponse("密碼已成功重設！"));
    }
}