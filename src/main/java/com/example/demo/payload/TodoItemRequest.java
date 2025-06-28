package com.example.demo.payload;

import com.example.demo.model.entity.TodoItem; // 確保導入 TodoItem 以使用其枚舉
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoItemRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank // 確保狀態也不是空的
    private String status; // ⭐ 關鍵修正：將類型改回 String ⭐

    @NotBlank // 確保優先級也不是空的
    private String priority; // ⭐ 關鍵修正：將類型改回 String ⭐

    private LocalDate dueDate;

    @NotNull
    private Long projectId;

    // 負責人 ID，如果存在
    private AssignedToRequest assignedTo;

    // Getter 和 Setter 方法
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() { // ⭐ 這裡返回 String ⭐
        return status;
    }

    public void setStatus(String status) { // ⭐ 這裡接收 String ⭐
        this.status = status;
    }

    public String getPriority() { // ⭐ 這裡返回 String ⭐
        return priority;
    }

    public void setPriority(String priority) { // ⭐ 這裡接收 String ⭐
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public AssignedToRequest getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(AssignedToRequest assignedTo) {
        this.assignedTo = assignedTo;
    }

    // 內部類，用於處理 assignedTo
    public static class AssignedToRequest {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}