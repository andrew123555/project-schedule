package com.example.demo.response;

import com.example.demo.model.entity.UserActivity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty; 

import java.time.LocalDateTime;

public class UserActivityResponse {

    private Long id;
    private String username;
    private String action;
    
    // ⭐ 修正點：直接使用 actionType，與 UserActivity 實體保持一致 ⭐
    private UserActivity.ActionType actionType; // 對應 UserActivity 實體的 actionType 枚舉

    private String details;
    private String ipAddress;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public UserActivityResponse() {
    }

    public UserActivityResponse(UserActivity userActivity) {
        this.id = userActivity.getId();
        this.username = userActivity.getUsername();
        this.action = userActivity.getAction();
        this.actionType = userActivity.getActionType(); // ⭐ 修正點：直接獲取 actionType 枚舉 ⭐
        this.details = userActivity.getDetails();
        this.ipAddress = userActivity.getIpAddress();
        this.timestamp = userActivity.getTimestamp();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    // ⭐ 修正點：getter/setter 針對 actionType 欄位 ⭐
    public UserActivity.ActionType getActionType() { return actionType; }
    public void setActionType(UserActivity.ActionType actionType) { this.actionType = actionType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}