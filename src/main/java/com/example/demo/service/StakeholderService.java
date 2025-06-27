package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Stakeholder; // 確保這是您的 Stakeholder 實體路徑
import com.example.demo.payload.StakeholderRequest; // 確保這是您的 StakeholderRequest DTO 路徑
import com.example.demo.repository.StakeholderRepository;
import com.example.demo.response.StakeholderResponse; // 確保這是您的 StakeholderResponse DTO 路徑

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StakeholderService {

    @Autowired
    private StakeholderRepository stakeholderRepository;

    // 假設您有一個 ProjectRepository 或 ProjectService 來獲取專案名稱
    // @Autowired
    // private ProjectRepository projectRepository;

    public StakeholderResponse createStakeholder(StakeholderRequest stakeholderRequest) {
        Stakeholder stakeholder = new Stakeholder();
        stakeholder.setName(stakeholderRequest.getName());
        stakeholder.setPhone(stakeholderRequest.getPhone());
        stakeholder.setEmail(stakeholderRequest.getEmail());
        stakeholder.setRequirement(stakeholderRequest.getRequirement());
        stakeholder.setPower(stakeholderRequest.isPower());
        stakeholder.setInterest(stakeholderRequest.isInterest());
        // ⭐ 修正：從 getMatrixStatus() 獲取，而不是 getMatrix_status() ⭐
        stakeholder.setMatrixStatus(stakeholderRequest.getMatrixStatus());
        stakeholder.setProjectId(stakeholderRequest.getProjectId());

        @SuppressWarnings("unchecked") // 抑制 CrudRepository.save() 可能產生的泛型警告
        Stakeholder savedStakeholder = stakeholderRepository.save(stakeholder);
        return convertToResponse(savedStakeholder);
    }

    public List<StakeholderResponse> getStakeholdersByProjectId(Long projectId) {
        List<Stakeholder> stakeholders = stakeholderRepository.findByProjectId(projectId);
        return stakeholders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StakeholderResponse getStakeholderById(Long id) {
        Stakeholder stakeholder = stakeholderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + id));
        return convertToResponse(stakeholder);
    }

    public StakeholderResponse updateStakeholder(Long id, StakeholderRequest stakeholderRequest) {
        Stakeholder existingStakeholder = stakeholderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + id));

        existingStakeholder.setName(stakeholderRequest.getName());
        existingStakeholder.setPhone(stakeholderRequest.getPhone());
        existingStakeholder.setEmail(stakeholderRequest.getEmail());
        existingStakeholder.setRequirement(stakeholderRequest.getRequirement());
        existingStakeholder.setPower(stakeholderRequest.isPower());
        existingStakeholder.setInterest(stakeholderRequest.isInterest());
        // ⭐ 修正：從 getMatrixStatus() 獲取，而不是 getMatrix_status() ⭐
        existingStakeholder.setMatrixStatus(stakeholderRequest.getMatrixStatus());
        // 通常編輯時不允許修改 projectId，如果需要，請根據業務邏輯處理
        // existingStakeholder.setProjectId(stakeholderRequest.getProjectId());

        @SuppressWarnings("unchecked") // 抑制 CrudRepository.save() 可能產生的泛型警告
        Stakeholder updatedStakeholder = stakeholderRepository.save(existingStakeholder);
        return convertToResponse(updatedStakeholder);
    }

    public void deleteStakeholder(Long id) {
        if (!stakeholderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stakeholder not found with id " + id);
        }
        stakeholderRepository.deleteById(id);
    }

    // Helper method to convert Entity to Response DTO
    private StakeholderResponse convertToResponse(Stakeholder stakeholder) {
        StakeholderResponse response = new StakeholderResponse();
        response.setId(stakeholder.getId());
        response.setName(stakeholder.getName());
        response.setPhone(stakeholder.getPhone());
        response.setEmail(stakeholder.getEmail());
        response.setRequirement(stakeholder.getRequirement());
        response.setPower(stakeholder.isPower());
        response.setInterest(stakeholder.isInterest());
        // ⭐ 修正：設置 setMatrixStatus()，而不是 setMatrix_status() ⭐
        response.setMatrixStatus(stakeholder.getMatrixStatus());
        response.setProjectId(stakeholder.getProjectId());
        // 如果需要顯示專案名稱，您需要在這裡通過 ProjectService/Repository 獲取
        // 例如: projectRepository.findById(stakeholder.getProjectId()).map(Project::getName).orElse("Unknown Project")
        response.setProjectName("Project " + stakeholder.getProjectId()); // 暫時寫死或替換為實際邏輯
        return response;
    }
}