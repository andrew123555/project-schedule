package com.example.demo.controller; // 確保這個套件名稱與您的實際專案匹配

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600) // 允許前端的跨域請求
@RestController // ⭐ 關鍵：標記這個類別是一個 RESTful API 控制器
@RequestMapping("/api/test") // ⭐ 關鍵：定義這個控制器所有方法的基礎路徑
public class TestController {

    @GetMapping("/all") // ⭐ 關鍵：定義處理 GET /api/test/all 請求的方法
    public String allAccess() {
        return "Public Content.";
    }

    // 您可能還有其他測試端點，例如：
    // @GetMapping("/user")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    // public String userAccess() {
    //     return "User Content.";
    // }
    // ... 等等
}