package com.example.demo.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TodoItemRequest {

    @NotBlank(message = "標題不能為空")
    @Size(max = 255, message = "標題長度不能超過 255 個字符")
    private String title;

    private String type;

    private String description;

    @NotBlank(message = "狀態不能為空")
    private String status; // 使用 String 類型，後端再轉換為枚舉

    @NotNull(message = "優先級不能為空")
    private Integer priority;

    @NotNull(message = "到期日不能為空")
    private LocalDateTime dueDate;

    // ⭐ 新增欄位：部門 ⭐
    @Size(max = 100, message = "部門長度不能超過 100 個字符")
    private String department;

    // ⭐ 新增欄位：負責人 ID ⭐
    private Long assigneeId; // 負責人的用戶 ID

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    // ⭐ 新增 Department 的 Getter 和 Setter ⭐
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // ⭐ 新增 AssigneeId 的 Getter 和 Setter ⭐
    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}