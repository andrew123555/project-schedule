package com.example.demo.controller;

import com.example.demo.response.UserInfoResponse; // 導入您的 UserInfoResponse DTO
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/users") // 用戶相關 API 的通用路徑
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping // GET /api/users
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')") // 限制對獲取所有用戶的存取
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        List<UserInfoResponse> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(users); // 200 OK
    }

    // 您可能還會添加用於獲取單個用戶、更新用戶等的端點。
}