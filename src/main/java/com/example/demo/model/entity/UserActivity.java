package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import com.example.demo.converter.ActionTypeConverter;

// 如果您之前在其他實體中使用了 Lombok 的 @Data，建議在這裡也添加
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
@Entity
@Table(name = "user_activities") // 定義資料庫表名
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // 執行操作的使用者名稱

    @Column(length = 500) 
    private String action; // 例如："使用者登入", "專案創建" 等更詳細的描述

    @Enumerated(EnumType.STRING)
    @Column(length = 50) // 設定為 50 個字符長
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
    public UserActivity(String username, String action, ActionType actionType, String details, String ipAddress) {
        this.username = username;
        this.action = action;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // setActionType 方法現在期望接收 UserActivity.ActionType 類型
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) { // <-- 參數類型是內部 ActionType
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
    	
    	user_management,
        login_success,
        login_failed,
        logout,
        register,
        create_reoject,
        update_project,
        delete_project,
        view_all_projects,
		create_stakeholder,
		update_stakeholder,
		delete_stakeholder,
		view_stakeholders,
		create_todo_item,
		update_todo_item,
		delete_todo_item,
		view_todo_items,
		api_access,
		user_profile_update,
		password_change,
		create_project_item,
		update_project_item,
		delete_project_item,
		todo_item_management,
		project_management,
		error
     
    }
}