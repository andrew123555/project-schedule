package com.example.demo.response;


import java.time.Instant; // 與 Request 中保持一致
import java.time.LocalDateTime;
import java.time.ZoneOffset; // 用於 Instant 到特定時區的轉換，如果需要

public class TodoItemResponse {

    private Long id; // ID 通常由資料庫生成，Response 中包含
    private String name;
    private String category;
    private Instant startTime;
    private Instant endTime;
    private Long projectId;
    private Long assigneeId;
    private String assigneeUsername; // 可能會包含負責人的用戶名，方便前端顯示
    private Boolean confidential;
    private Boolean completed; // 待辦事項是否完成的狀態

    // 無參建構子是必要的
    public TodoItemResponse() {
    }

    // 帶所有參數的建構子，方便從實體轉換
    public TodoItemResponse(Long id, String name, String category, Instant startTime, Instant endTime, Long projectId, Long assigneeId, String assigneeUsername, Boolean confidential, Boolean completed) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.assigneeUsername = assigneeUsername;
        this.confidential = confidential;
        this.completed = completed;
    }

    // --- Getter 和 Setter 方法 ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeUsername() {
        return assigneeUsername;
    }

    public void setAssigneeUsername(String assigneeUsername) {
        this.assigneeUsername = assigneeUsername;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "TodoItemResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", projectId=" + projectId +
                ", assigneeId=" + assigneeId +
                ", assigneeUsername='" + assigneeUsername + '\'' +
                ", confidential=" + confidential +
                ", completed=" + completed +
                '}';
    }

    // 可以添加一個從 TodoItem 實體轉換為 TodoItemResponse 的靜態方法，方便Service層使用
    // 前提是您的 TodoItem 實體已經存在且有對應的 getter 方法
    /*
    public static TodoItemResponse fromEntity(TodoItem todoItem, String assigneeUsername) {
        if (todoItem == null) return null;
        return new TodoItemResponse(
            todoItem.getId(),
            todoItem.getName(),
            todoItem.getCategory(),
            todoItem.getStartTime(),
            todoItem.getEndTime(),
            todoItem.getProject() != null ? todoItem.getProject().getId() : null,
            todoItem.getAssignee() != null ? todoItem.getAssignee().getId() : null,
            assigneeUsername, // 這裡傳入負責人的用戶名
            todoItem.getConfidential(),
            todoItem.getCompleted()
        );
    }
    */
}
