package com.example.demo.controller;

import com.example.demo.model.entity.UserActivity; 
import com.example.demo.payload.ProjectRequest;
import com.example.demo.response.MessageResponse; 
import com.example.demo.response.ProjectResponse; 
import com.example.demo.service.ProjectService;
import com.example.demo.service.UserActivityService; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired 
    private UserActivityService userActivityService; 

    @GetMapping
    //@PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(HttpServletRequest request) {
        userActivityService.recordActivity(
            UserActivity.ActionType.api_access, 
            "查看所有專案", 
            "查看了所有專案列表", 
            request.getRemoteAddr() 
        );
        List<ProjectResponse> projects = projectService.getAllProjects(); 
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id, HttpServletRequest request) {
        userActivityService.recordActivity(
            UserActivity.ActionType.api_access, 
            "查看單個專案", 
            "查看了專案 ID: " + id, 
            request.getRemoteAddr() 
        );
        ProjectResponse project = projectService.getProjectById(id); 
        return ResponseEntity.ok(project);
    }

    @PostMapping
    //@PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest, HttpServletRequest request) {
        ProjectResponse createdProject = projectService.createProject(projectRequest); 
        userActivityService.recordActivity(
            UserActivity.ActionType.project_create, 
            "創建專案", 
            "創建了專案: " + createdProject.getName(), 
            request.getRemoteAddr() 
        );
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectRequest, HttpServletRequest request) {
        ProjectResponse updatedProject = projectService.updateProject(id, projectRequest); 
        userActivityService.recordActivity(
            UserActivity.ActionType.project_update, 
            "更新專案", 
            "更新了專案 ID: " + id + " (名稱: " + updatedProject.getName() + ")", 
            request.getRemoteAddr() 
        );
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseEntity<MessageResponse> deleteProject(@PathVariable Long id, HttpServletRequest request) {
        projectService.deleteProject(id);
        userActivityService.recordActivity(
            UserActivity.ActionType.project_delete, 
            "刪除專案", 
            "刪除了專案 ID: " + id, 
            request.getRemoteAddr() 
        );
        return ResponseEntity.ok(new MessageResponse("專案刪除成功！"));
    }

    @PostMapping("/{projectId}/add-stakeholder/{stakeholderId}")
    //@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<MessageResponse> addStakeholderToProject(
            @PathVariable Long projectId,
            @PathVariable Long stakeholderId,
            HttpServletRequest request) {
        projectService.addStakeholderToProject(projectId, stakeholderId);
        userActivityService.recordActivity(
            UserActivity.ActionType.stakeholder_create, 
            "添加利害關係人到專案",
            "將利害關係人 ID: " + stakeholderId + " 添加到專案 ID: " + projectId,
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(new MessageResponse("利害關係人已成功添加到專案！"));
    }

    @DeleteMapping("/{projectId}/remove-stakeholder/{stakeholderId}")
    //@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<MessageResponse> removeStakeholderFromProject(
            @PathVariable Long projectId,
            @PathVariable Long stakeholderId,
            HttpServletRequest request) {
        projectService.removeStakeholderFromProject(projectId, stakeholderId);
        userActivityService.recordActivity(
            UserActivity.ActionType.stakeholder_remove_from_project,
            "從專案中移除利害關係人",
            "將利害關係人 ID: " + stakeholderId + " 從專案 ID: " + projectId + " 移除",
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(new MessageResponse("利害關係人已成功從專案中移除！"));
    }
}