package com.example.demo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activities")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // 新增：用戶 ID 欄位

    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "action", nullable = false)
    private String action; // 動作描述，例如 "用戶登入成功", "創建專案"

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // 詳細資訊，例如 "用戶: testuser", "專案: ProjectX"

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "timestamp")
    private LocalDateTime timestamp; // 確保類型正確

    // ⭐ 關鍵修正：擴展 ActionType 枚舉 ⭐
    public enum ActionType {
        login_success,
        login_failure,
        register_success,
        register_failure,
        logout,
        project_create,
        project_update,
        project_delete,
        todo_create, // 保持為 todo_create
        todo_update, // 保持為 todo_update
        todo_delete, // 保持為 todo_delete
        todo_view, // ⭐ 新增：用於查看待辦事項 ⭐
        stakeholder_create,
        stakeholder_update,
        stakeholder_delete,
        stakeholder_add_to_project,
        stakeholder_remove_from_project,
        user_role_update, // ⭐ 新增：用於更新用戶角色 ⭐
        user_view_all, // ⭐ 新增：用於查看所有用戶列表 ⭐
        api_access, // 一般 API 訪問
        admin_action, // 管理員操作，例如刪除日誌
        error // 應用程式錯誤
    }

    // Constructors
    public UserActivity() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters (保持不變，已在上次修正中添加 userId 的 getter/setter)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}