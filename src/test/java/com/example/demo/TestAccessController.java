package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestAccessController {

    @GetMapping("/admin/test")
    public String adminOnly() {
        return "歡迎管理者，這是 /admin/test";
    }

    @GetMapping("/user/test")
    public String userOnly() {
        return "哈囉使用者，這是 /user/test";
    }

    @GetMapping("/home")
    public String home() {
        return "登入成功，這是首頁";
    }
}