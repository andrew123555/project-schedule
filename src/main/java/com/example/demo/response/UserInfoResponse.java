// src/main/java/com/example/demo/response/UserInfoResponse.java (如果不存在)
package com.example.demo.response;

import java.util.List;


public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String accessToken; // 為了前端方便，登入時會包含此字段

    public UserInfoResponse(Long id, String username, String email, List<String> roles, String accessToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.accessToken = accessToken;
    }
    
 // ⭐ 新增一個接受四個參數的建構子 ⭐
    public UserInfoResponse(Long id, String username, String email, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.accessToken = null; // 在這個建構子中，accessToken 設置為 null
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}