package com.example.demo.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant; // 使用 Instant 來表示時間戳，更推薦用於後端

public class TodoItemRequest {

    @NotBlank(message = "待辦事項名稱不能為空")
    @Size(max = 100, message = "待辦事項名稱不能超過100個字元")
    private String name;

    @NotBlank(message = "類別不能為空")
    @Size(max = 50, message = "類別不能超過50個字元")
    private String category;

    @NotNull(message = "開始時間不能為空")
    private Instant startTime; // 使用 Instant 來接收 ISO 8601 格式的日期時間

    @NotNull(message = "結束時間不能為空")
    private Instant endTime;   // 使用 Instant 來接收 ISO 8601 格式的日期時間

    @NotNull(message = "專案ID不能為空")
    private Long projectId;

    // 負責人可以是可選的，所以使用 Long 而不是原始類型 long
    // 如果是可選的，不需要 @NotNull，但在業務邏輯中處理 null
    private Long assigneeId;

    @NotNull(message = "機密性不能為空")
    private Boolean confidential;

    // 無參建構子是必要的，供 Spring 反序列化 JSON 使用
    public TodoItemRequest() {
    }

    // 帶參數的建構子，方便測試或手動創建
    public TodoItemRequest(String name, String category, Instant startTime, Instant endTime, Long projectId, Long assigneeId, Boolean confidential) {
        this.name = name;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.confidential = confidential;
    }

    // --- Getter 和 Setter 方法 ---

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

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    @Override
    public String toString() {
        return "TodoItemRequest{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", projectId=" + projectId +
                ", assigneeId=" + assigneeId +
                ", confidential=" + confidential +
                '}';
    }
}