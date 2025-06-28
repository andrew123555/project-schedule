package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.Stakeholder;
import com.example.demo.model.entity.User; // 導入 User 實體
import com.example.demo.payload.ProjectRequest;
import com.example.demo.response.ProjectResponse;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StakeholderRepository;
import com.example.demo.repository.UserRepository; // 導入 UserRepository
import com.example.demo.service.UserDetailsImpl; // 導入 UserDetailsImpl

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // 導入 Authentication
import org.springframework.security.core.context.SecurityContextHolder; // 導入 SecurityContextHolder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private UserRepository userRepository; // ⭐ 新增：注入 UserRepository ⭐

    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(ProjectResponse::new)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id) 
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        return new ProjectResponse(project);
    }

    public ProjectResponse createProject(ProjectRequest projectRequest) {
        // ⭐ 關鍵修正：獲取當前認證的用戶並設置到專案中 ⭐
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 獲取當前登入的用戶名

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Project project = new Project();
        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStatus(projectRequest.getStatus());
        project.setUser(currentUser); // ⭐ 將當前用戶設置為專案的創建者 ⭐

        if (projectRequest.getStakeholderIds() != null && !projectRequest.getStakeholderIds().isEmpty()) {
            Set<Stakeholder> stakeholders = new HashSet<>();
            for (Long stakeholderId : projectRequest.getStakeholderIds()) {
                Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));
                stakeholders.add(stakeholder);
            }
            project.setStakeholders(stakeholders);
        }

        Project savedProject = projectRepository.save(project);
        return new ProjectResponse(savedProject);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest projectRequest) {
        Project project = projectRepository.findById(id) 
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));

        project.setName(projectRequest.getName());
        project.setDescription(projectRequest.getDescription());
        project.setStatus(projectRequest.getStatus());

        if (projectRequest.getStakeholderIds() != null) {
            Set<Stakeholder> newStakeholders = new HashSet<>();
            for (Long stakeholderId : projectRequest.getStakeholderIds()) {
                Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));
                newStakeholders.add(stakeholder);
            }
            project.setStakeholders(newStakeholders);
        } else {
            project.setStakeholders(new HashSet<>()); 
        }

        Project updatedProject = projectRepository.save(project);
        return new ProjectResponse(updatedProject);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        
        new HashSet<>(project.getStakeholders()).forEach(stakeholder -> stakeholder.getProjects().remove(project));
        project.getStakeholders().clear(); 
        
        projectRepository.delete(project);
    }

    public void addStakeholderToProject(Long projectId, Long stakeholderId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));

        project.addStakeholder(stakeholder);
        projectRepository.save(project);
    }

    public void removeStakeholderFromProject(Long projectId, Long stakeholderId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));

        project.removeStakeholder(stakeholder);
        projectRepository.save(project);
    }
}