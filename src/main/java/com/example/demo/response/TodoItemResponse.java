package com.example.demo.response;

import com.example.demo.model.entity.TodoItem;
import com.example.demo.model.entity.User; // 確保導入 User 以處理 assignedTo

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List; // 如果您在 TodoItemResponse 中有 List 類型的字段

public class TodoItemResponse {
    private Long id;
    private String title;
    private String description;
    private String status; // ⭐ 修正：將類型改為 String ⭐
    private String priority; // ⭐ 修正：將類型改為 String ⭐
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt; // ⭐ 修正：使用 updatedAt 而不是 lastModifiedAt ⭐
    private Long projectId;
    private String projectName; // 新增字段，用於顯示專案名稱
    private UserInfoResponse assignedTo; // 用於顯示負責人信息

    public TodoItemResponse(TodoItem todoItem) {
        this.id = todoItem.getId();
        this.title = todoItem.getTitle();
        this.description = todoItem.getDescription();
        // ⭐ 修正：從枚舉獲取字串值 ⭐
        this.status = todoItem.getStatus() != null ? todoItem.getStatus().name() : null; 
        // ⭐ 修正：從枚舉獲取字串值 ⭐
        this.priority = todoItem.getPriority() != null ? todoItem.getPriority().name() : null; 
        this.dueDate = todoItem.getDueDate();
        this.createdAt = todoItem.getCreatedAt();
        this.updatedAt = todoItem.getUpdatedAt(); // ⭐ 修正：使用 getUpdatedAt() ⭐
        
        
        this.projectName = todoItem.getProject() != null ? todoItem.getProject().getName() : null; // 設置專案名稱

        // 處理 assignedTo
        if (todoItem.getAssignedTo() != null) {
            User assignedUser = todoItem.getAssignedTo();
            this.assignedTo = new UserInfoResponse(
                assignedUser.getId(),
                assignedUser.getUsername(),
                assignedUser.getEmail(),
                null, // roles, 如果不需要可以傳 null
                null  // projects, 如果不需要可以傳 null
            );
        } else {
            this.assignedTo = null;
        }
        
        this.projectId = todoItem.getProject().getId();
        // ⭐ 關鍵修正：對 project 進行空值檢查 ⭐
        if (todoItem.getProject() != null) {
            this.projectName = todoItem.getProject().getName();
        } else {
            this.projectName = null;
        }
    }

    // --- Getter 方法 ---
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() { // ⭐ 返回 String ⭐
        return status;
    }

    public String getPriority() { // ⭐ 返回 String ⭐
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() { // ⭐ 確保有這個 getter ⭐
        return updatedAt;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public UserInfoResponse getAssignedTo() {
        return assignedTo;
    }

    // Setter 方法 (如果需要，通常 Response 對象不需要 setter)
    // 這裡為了完整性，可以選擇添加，但對於響應對象通常是只讀的
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public void setAssignedTo(UserInfoResponse assignedTo) { this.assignedTo = assignedTo; }
}