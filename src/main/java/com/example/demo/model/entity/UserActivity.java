package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activities") // 定義資料庫表名
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // 執行操作的使用者名稱

    // ⭐ 新增這個 'action' 字段，因為錯誤訊息提到了它
    @Column(nullable = false) // 如果數據庫中 'action' 列也是 NOT NULL
    private String action; // 例如："使用者登入", "專案創建" 等更詳細的描述

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false) // 確保這裡的 name 與數據庫列名匹配
    private ActionType actionType; // 操作類型 (例如 LOGIN_SUCCESS, CREATE_PROJECT)

    @Column(columnDefinition = "TEXT") // 儲存詳細資訊，允許較長內容
    private String details; // 操作的具體細節 (例如 "新增了項目: ProjectX")

    @Column(nullable = false)
    private LocalDateTime timestamp; // 操作發生的時間

    private String ipAddress; // 操作者的 IP 地址

    // 無參數建構子 (JPA 需要)
    public UserActivity() {
    }

    // 常用建構子
    // ⭐ 更新建構子以包含新的 'action' 字段
    public UserActivity(String username, String action, ActionType actionType, String details, String ipAddress) {
        this.username = username;
        this.action = action; // ⭐ 初始化 action
        this.actionType = actionType;
        this.details = details;
        this.timestamp = LocalDateTime.now(); // 自動設定當前時間
        this.ipAddress = ipAddress;
    }

    // --- Getters and Setters ---
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

    // ⭐ 新增 action 的 Getter 和 Setter
 // ⭐ Getter 和 Setter for 'action'
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action; // ⭐ 將這裡修正為 this.action = action;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // --- ActionType Enum ---
    public enum ActionType {
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGOUT,
        REGISTER,
        CREATE_PROJECT,
        UPDATE_PROJECT,
        DELETE_PROJECT,
        VIEW_ALL_PROJECTS,
        CREATE_PROJECT_ITEM,
        UPDATE_PROJECT_ITEM,
        DELETE_PROJECT_ITEM,
        VIEW_PROJECT_ITEMS_BY_ID,
        API_ACCESS,
        USER_PROFILE_UPDATE,
        PASSWORD_CHANGE,
    }
}