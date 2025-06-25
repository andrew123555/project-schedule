package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Project;
import com.example.demo.payload.ProjectRequest;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.response.ProjectResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // Helper method to convert Project Entity to ProjectResponse DTO
    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus());
        return response;
    }

    // Helper method to convert ProjectRequest DTO to Project Entity (for creation/update)
    private Project convertToEntity(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());
        return project;
    }

    /**
     * 創建一個新專案
     * @param projectRequest 包含新專案數據的請求 DTO
     * @return 創建的專案的響應 DTO
     */
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        Project project = convertToEntity(projectRequest);
        @SuppressWarnings("unchecked") // 抑制可能出現的泛型警告
        Project savedProject = projectRepository.save(project);
        return convertToResponse(savedProject);
    }

    /**
     * 根據 ID 獲取單個專案
     * @param id 專案 ID
     * @return 匹配專案的響應 DTO
     * @throws ResourceNotFoundException 如果找不到專案
     */
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        return convertToResponse(project);
    }

    /**
     * 獲取所有專案
     * @return 所有專案的響應 DTO 列表
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 更新現有專案
     * @param id 要更新的專案 ID
     * @param projectRequest 包含更新數據的請求 DTO
     * @return 更新後的專案的響應 DTO
     * @throws ResourceNotFoundException 如果找不到專案
     */
    public ProjectResponse updateProject(Long id, ProjectRequest projectRequest) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));

        existingProject.setName(projectRequest.getName());
        existingProject.setDescription(projectRequest.getDescription());
        existingProject.setStatus(projectRequest.getStatus());
        // 可以添加更多的欄位更新邏輯

        @SuppressWarnings("unchecked") // 抑制可能出現的泛型警告
        Project updatedProject = projectRepository.save(existingProject);
        return convertToResponse(updatedProject);
    }

    /**
     * 根據 ID 刪除專案
     * @param id 要刪除的專案 ID
     * @throws ResourceNotFoundException 如果找不到專案
     */
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id " + id);
        }
        projectRepository.deleteById(id);
    }
}