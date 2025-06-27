package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service 
@Transactional // 確保服務層方法在事務中執行
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Project> getAllProjects() {
        System.out.println("ProjectService: 正在獲取所有專案 (包含用戶信息)。");
        return projectRepository.findAll(); 
    }

    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    public Optional<Project> getProjectById(Long id) {
        System.out.println("ProjectService: 正在嘗試從資料庫獲取專案 ID: " + id);
        Optional<Project> projectOptional = projectRepository.findById(id);
        
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            System.out.println("ProjectService: 成功獲取專案物件: ID=" + project.getId() + ", Name=" + project.getName());
            if (project.getUser() != null) {
                System.out.println("ProjectService: 專案用戶存在，用戶名: " + project.getUser().getUsername());
                project.getUser().getUsername(); 
            } else {
                System.out.println("ProjectService: 專案用戶為空。");
            }
        } else {
            System.out.println("ProjectService: 未找到 ID 為 " + id + " 的專案。返回 Optional.empty()");
        }
        return projectOptional;
    }

    public Project createProject(Project project, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        project.setUser(user);
        project.setCreatedAt(LocalDateTime.now());
        project.setLastModifiedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project projectDetails, Long userId) {
        return projectRepository.findById(id).map(project -> {
            if (!project.getUser().getId().equals(userId)) {
                throw new ResourceNotFoundException("Project does not belong to user with id " + userId);
            }
            project.setName(projectDetails.getName());
            project.setDescription(projectDetails.getDescription());
            project.setLastModifiedAt(LocalDateTime.now());
            return projectRepository.save(project);
        }).orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
    }

    // ⭐ 修正點：確保 deleteProject 方法正確處理刪除，並移除不必要的返回值 ⭐
    public void deleteProject(Long id, Long userId) {
        projectRepository.findById(id).ifPresentOrElse(project -> { // 使用 ifPresentOrElse 更簡潔
            if (!project.getUser().getId().equals(userId)) {
                throw new ResourceNotFoundException("Project does not belong to user with id " + userId);
            }
            projectRepository.delete(project); // 執行刪除
            System.out.println("ProjectService: 成功刪除了專案 ID: " + id); // 添加日誌
        }, () -> {
            throw new ResourceNotFoundException("Project not found with id " + id);
        });
    }
}