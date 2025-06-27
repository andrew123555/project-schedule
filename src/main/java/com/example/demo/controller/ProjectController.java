package com.example.demo.controller;

import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.UserActivity;
import com.example.demo.payload.ProjectRequest;
import com.example.demo.response.ProjectResponse;
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserActivityService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.ResourceNotFoundException; // 導入 ResourceNotFoundException

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/project") 
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping 
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() { 
        List<Project> projects = projectService.getAllProjects(); 
        
        if (projects.isEmpty()) {
            userActivityService.logUserActivity(getCurrentUsername(), "查看了所有專案 (無內容)", UserActivity.ActionType.project_management);
            return ResponseEntity.noContent().build(); 
        }
        
        List<ProjectResponse> responses = projects.stream()
                .map(ProjectResponse::new) 
                .collect(Collectors.toList());
        
        userActivityService.logUserActivity(getCurrentUsername(), "查看了所有專案", UserActivity.ActionType.project_management);
        return ResponseEntity.ok(responses); 
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        System.out.println("ProjectController: 接收到獲取專案 ID: " + id + " 的請求。");
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> {
                    System.err.println("ProjectController: 未找到專案 ID: " + id + "，拋出 RuntimeException。");
                    userActivityService.logUserActivity(getCurrentUsername(), "操作失敗: getProjectById (未找到專案 ID: " + id + ")", UserActivity.ActionType.api_access);
                    return new RuntimeException("Project not found with id " + id);
                });
        
        System.out.println("ProjectController: 成功從服務層獲取專案物件。名稱: " + project.getName());
        userActivityService.logUserActivity(getCurrentUsername(), "查看了專案: " + project.getName(), UserActivity.ActionType.project_management);
        
        ProjectResponse response = new ProjectResponse(project);
        System.out.println("ProjectController: 已創建 ProjectResponse。名稱: " + response.getName());
        return ResponseEntity.ok(response); 
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping 
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        try {
            Long currentUserId = getCurrentUserId();
            Project newProject = new Project();
            newProject.setName(projectRequest.getName());
            newProject.setDescription(projectRequest.getDescription());

            Project savedProject = projectService.createProject(newProject, currentUserId);
            userActivityService.logUserActivity(getCurrentUsername(), "創建了專案: " + savedProject.getName(), UserActivity.ActionType.project_management);
            return new ResponseEntity<>(new ProjectResponse(savedProject), HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("創建專案時發生內部錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}") 
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectRequest) {
        try {
            Long currentUserId = getCurrentUserId();
            Project projectDetails = new Project();
            projectDetails.setName(projectRequest.getName());
            projectDetails.setDescription(projectRequest.getDescription());

            Project updatedProject = projectService.updateProject(id, projectDetails, currentUserId);
            userActivityService.logUserActivity(getCurrentUsername(), "更新了專案: " + updatedProject.getName(), UserActivity.ActionType.project_management);
            return ResponseEntity.ok(new ProjectResponse(updatedProject));
        } catch (Exception e) {
            System.err.println("更新專案時發生內部錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}") 
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            projectService.deleteProject(id, currentUserId);
            userActivityService.logUserActivity(getCurrentUsername(), "刪除了專案 ID: " + id,UserActivity.ActionType.project_management);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 成功刪除，返回 204
        } catch (ResourceNotFoundException e) { // ⭐ 修正點：捕獲 ResourceNotFoundException ⭐
            System.err.println("刪除專案失敗: " + e.getMessage());
            userActivityService.logUserActivity(getCurrentUsername(), "操作失敗: 刪除專案 (未找到或無權限刪除 ID: " + id + ")", UserActivity.ActionType.api_access);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 返回 404
        } catch (Exception e) {
            System.err.println("刪除專案時發生內部錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 返回 500
        }
    }

    private String getCurrentUsername() {
        try {
            return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (Exception e) {
            System.err.println("Error getting current username: " + e.getMessage());
            return "anonymousUser";
        }
    }

    private Long getCurrentUserId() {
        try {
            return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
            throw new RuntimeException("無法獲取當前用戶 ID，請登入。");
        }
    }
}