package com.example.demo.response;

import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.Stakeholder;
import com.example.demo.model.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set; // 導入 Set

public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId; // Creator user ID
    private String username; // Creator username
    private List<StakeholderResponse> stakeholders; // List of stakeholders

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.status = project.getStatus();        
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();

        // ⭐ 這裡對 project.getUser() 的訪問現在是安全的，因為它會被 EntityGraph 加載 ⭐
        if (project.getUser() != null) {
            this.userId = project.getUser().getId();
            this.username = project.getUser().getUsername();
        } else {
            this.userId = null;
            this.username = "N/A";
        }

        // ⭐ 這裡對 project.getStakeholders() 的訪問現在是安全的，因為它會被 EntityGraph 加載 ⭐
        if (project.getStakeholders() != null) {
            this.stakeholders = project.getStakeholders().stream()
                .map(StakeholderResponse::new) // 使用 StakeholderResponse 的構造函數
                .collect(Collectors.toList());
        } else {
            this.stakeholders = null;
        }
    }

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public List<StakeholderResponse> getStakeholders() {
        return stakeholders;
    }

    public void setStakeholders(List<StakeholderResponse> stakeholders) {
        this.stakeholders = stakeholders;
    }
}
