package com.example.demo.response;

import com.example.demo.model.entity.TodoItem;
import java.time.LocalDateTime;

public class TodoItemResponse {
    private Long id;
    private String title;
    private String type;
    private String description;
    private String status;
    private Integer priority;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private Long projectId;

    private String department;
    private String assigneeUsername; 
    private Long assigneeId; 

    public TodoItemResponse(TodoItem todoItem) {
        this.id = todoItem.getId();
        this.title = todoItem.getTitle();
        this.type = todoItem.getType();
        this.description = todoItem.getDescription();
        this.status = todoItem.getStatus().name(); 
        this.priority = todoItem.getPriority();
        this.dueDate = todoItem.getDueDate();
        this.createdAt = todoItem.getCreatedAt();
        this.lastModifiedAt = todoItem.getLastModifiedAt();
        this.projectId = todoItem.getProject().getId();

        this.department = todoItem.getDepartment();
        
        // 這個 null 檢查是正確的，但 LazyInitializationException 發生在 getUsername() 上
        // 這表示 todoItem.getAssignee() 返回的是一個未初始化的代理物件
        if (todoItem.getAssignee() != null) {
            this.assigneeUsername = todoItem.getAssignee().getUsername(); // 這裡會拋出異常
            this.assigneeId = todoItem.getAssignee().getId();
        } else {
            this.assigneeUsername = null;
            this.assigneeId = null;
        }
    }

    // Getters and Setters (省略，保持不變)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getAssigneeUsername() { return assigneeUsername; }
    public void setAssigneeUsername(String assigneeUsername) { this.assigneeUsername = assigneeUsername; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
}