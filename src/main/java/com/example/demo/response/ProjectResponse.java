package com.example.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.demo.model.entity.Project;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private Long userId; 
    private String username; 
    // 與 Project Entity 中的欄位匹配
    
    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.name = project.getName(); 
        this.description = project.getDescription();
        this.createdAt = project.getCreatedAt();
        this.lastModifiedAt = project.getLastModifiedAt();
        // 檢查 project.getUser() 是否為 null，以避免 LazyInitializationException
        // 即使在 ProjectService 中已經預加載，這裡做 null 檢查也是好習慣
        if (project.getUser() != null) {
            this.userId = project.getUser().getId();
            this.username = project.getUser().getUsername(); 
        } else {
            this.userId = null;
            this.username = null;
        }
    }
}