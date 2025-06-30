package com.example.demo.controller;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.payload.ForgotPasswordRequest; // 確保導入正確的 Request 物件
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.ResetPasswordRequest;
import com.example.demo.payload.SignupRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.response.MessageResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserActivityService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.exception.ResourceNotFoundException; // 導入 ResourceNotFoundException

import jakarta.servlet.http.HttpServletRequest; 
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // 導入 HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder; // 導入 SecurityContextHolder
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired 
    UserActivityService userActivityService; 

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest, request); 
            userActivityService.recordActivity(
                UserActivity.ActionType.login_success, 
                "用戶登入成功", 
                "用戶: " + loginRequest.getUsername(), 
                request.getRemoteAddr() 
            );
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
        	e.printStackTrace();
        	userActivityService.recordActivity(
                UserActivity.ActionType.login_failure, 
                "用戶登入失敗", 
                "用戶: " + loginRequest.getUsername() + ", 錯誤: " + e.getMessage(), 
                request.getRemoteAddr() 
            );
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("錯誤: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest, HttpServletRequest request) {
        try {
            authService.registerUser(signupRequest, request); 
            userActivityService.recordActivity(
                UserActivity.ActionType.register_success, 
                "用戶註冊成功", 
                "新用戶: " + signupRequest.getUsername(), 
                request.getRemoteAddr() 
            );
            return ResponseEntity.ok(new MessageResponse("用戶註冊成功！"));
        } catch (Exception e) {
            userActivityService.recordActivity(
                UserActivity.ActionType.error, 
                "用戶註冊失敗", 
                "用戶: " + signupRequest.getUsername() + ", 錯誤: " + e.getMessage(), 
                request.getRemoteAddr() 
            );
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("錯誤: " + e.getMessage()));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        Long userId = null; 
        try {
            // 從 SecurityContextHolder 獲取用戶ID
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetailsImpl) { // 確保 UserDetailsImpl 已導入
                UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                userId = userDetails.getId();
            }
            authService.logout(userId, request); // 傳遞 userId 和 request
            userActivityService.recordActivity(
                UserActivity.ActionType.logout, 
                "用戶登出", 
                "用戶登出成功", 
                request.getRemoteAddr() 
            );
            return ResponseEntity.ok(new MessageResponse("用戶登出成功！"));
        } catch (Exception e) {
            userActivityService.recordActivity(
                UserActivity.ActionType.error, 
                "用戶登出失敗", 
                "用戶登出失敗，錯誤: " + e.getMessage(), 
                request.getRemoteAddr() 
            );
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("錯誤: " + e.getMessage()));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.requestPasswordReset(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok(new MessageResponse("密碼重設連結已發送到您的電子郵件。請檢查您的收件箱。"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("請求密碼重設時發生錯誤: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            authService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("您的密碼已成功重設。"));
        } catch (ResourceNotFoundException e) {
            // 如果令牌無效或過期，返回 400 Bad Request (或 404 Not Found 取決於您的設計)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            // 處理其他意外錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("重設密碼時發生錯誤: " + e.getMessage()));
        }
    }
}