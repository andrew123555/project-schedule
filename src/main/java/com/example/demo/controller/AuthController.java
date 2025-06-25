package com.example.demo.controller;

import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.RegisterRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.response.MessageResponse;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository; // 注入 RoleRepository

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("收到登入請求 - 帳號：" + loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ⭐ 獲取 UserDetailsImpl 物件，其中包含用戶的所有資訊
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // ⭐ 這裡：使用 jwtUtils.generateTokenFromUsername 方法來生成 JWT 字串
            // validateJwtToken 是用來驗證已有的 token，而不是在登入時生成新的。
            String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // 返回 JWT 和用戶信息給前端，包括角色
            return ResponseEntity.ok(new JwtResponse(
                jwt, // 將生成的 JWT 字串傳入
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));

        } catch (AuthenticationException e) {
            System.err.println("認證失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(new MessageResponse("錯誤: 無效的帳號或密碼!"));
        } catch (Exception e) {
            System.err.println("登入過程中發生未知錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new MessageResponse("錯誤: 伺服器內部錯誤!"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("錯誤: 該帳號已被使用!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("錯誤: 該 Email 已被使用!"));
        }

        // 創建新用戶帳號並加密密碼
        User user = new User(signUpRequest.getUsername(),
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        // 預設給予 ROLE_USER 角色
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("錯誤: 未找到角色 USER."));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user); // 儲存到資料庫

        return ResponseEntity.ok(new MessageResponse("用戶註冊成功!"));
    }

    // 示例：一個需要認證才能訪問的端點
    @GetMapping("/test/user")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // 可以使用 @PreAuthorize 限制角色
    public String userAccess() {
        return "歡迎! 你是已登入的用戶。";
    }
}