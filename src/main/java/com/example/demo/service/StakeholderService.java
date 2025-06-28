package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.Stakeholder;
import com.example.demo.model.entity.User;
import com.example.demo.response.StakeholderResponse; 
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.StakeholderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StakeholderService {

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private ProjectRepository projectRepository; 

    @Autowired
    private UserRepository userRepository; 

    private StakeholderResponse convertToDto(Stakeholder stakeholder) {
        StakeholderResponse dto = new StakeholderResponse();
        dto.setId(stakeholder.getId());
        dto.setName(stakeholder.getName());
        dto.setEmail(stakeholder.getEmail());
        dto.setRole(stakeholder.getRole()); 
        dto.setContactInfo(stakeholder.getContactInfo()); 
        dto.setPhone(stakeholder.getPhone()); 
        dto.setRequirement(stakeholder.getRequirement()); 
        dto.setPower(stakeholder.getPower()); 
        dto.setInterest(stakeholder.getInterest()); 
        dto.setMatrixStatus(stakeholder.getMatrixStatus()); 
        
        if (stakeholder.getUser() != null) {
            dto.setUserId(stakeholder.getUser().getId());
            dto.setUsername(stakeholder.getUser().getUsername()); 
        }
        return dto;
    }

    public List<StakeholderResponse> getAllStakeholders() {
        return stakeholderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<StakeholderResponse> getStakeholdersByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        
        // 確保這裡的 stakeholders 集合在事務中被加載
        return project.getStakeholders().stream()
                .map(this::convertToDto) 
                .collect(Collectors.toList());
    }

    public Optional<StakeholderResponse> getStakeholderById(Long projectId, Long stakeholderId) {
        Optional<Stakeholder> stakeholderOptional = stakeholderRepository.findById(stakeholderId);

        if (stakeholderOptional.isPresent()) {
            Stakeholder stakeholder = stakeholderOptional.get();
            // 檢查該利害關係人是否確實屬於指定的專案
            boolean belongsToProject = stakeholder.getProjects().stream()
                                                 .anyMatch(p -> p.getId().equals(projectId));
            
            if (belongsToProject) {
                return Optional.of(convertToDto(stakeholder)); 
            } else {
                return Optional.empty(); 
            }
        } else {
            return Optional.empty(); 
        }
    }

    public StakeholderResponse createStakeholder(Long projectId, Stakeholder stakeholder, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        stakeholder.setUser(user);
        Stakeholder savedStakeholder = stakeholderRepository.save(stakeholder);

        project.addStakeholder(savedStakeholder); 
        projectRepository.save(project); 

        return convertToDto(savedStakeholder); 
    }

    public StakeholderResponse updateStakeholder(Long stakeholderId, Stakeholder stakeholderDetails) {
        return stakeholderRepository.findById(stakeholderId)
                .map(stakeholder -> {
                    stakeholder.setName(stakeholderDetails.getName());
                    stakeholder.setEmail(stakeholderDetails.getEmail());
                    stakeholder.setInterest(stakeholderDetails.getInterest());
                    stakeholder.setMatrixStatus(stakeholderDetails.getMatrixStatus());
                    stakeholder.setPhone(stakeholderDetails.getPhone());
                    stakeholder.setPower(stakeholderDetails.getPower());
                    stakeholder.setRequirement(stakeholderDetails.getRequirement());
                    stakeholder.setRole(stakeholderDetails.getRole()); 
                    stakeholder.setContactInfo(stakeholderDetails.getContactInfo()); 
                    return convertToDto(stakeholderRepository.save(stakeholder)); 
                }).orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));
    }

    // ⭐ 關鍵修正點：確保 deleteStakeholder 在刪除實體前解除所有專案關聯 ⭐
    public void deleteStakeholder(Long stakeholderId) {
        stakeholderRepository.findById(stakeholderId).ifPresent(stakeholder -> {
            // 遍歷所有關聯的專案，並從這些專案中移除該利害關係人
            // 這裡需要使用一個副本來避免 ConcurrentModificationException
            new HashSet<>(stakeholder.getProjects()).forEach(project -> project.removeStakeholder(stakeholder));
            
            // 清除利害關係人對專案的引用 (雖然上面已經處理，但明確清除更好)
            stakeholder.getProjects().clear(); 
            
            // 最後刪除利害關係人實體
            stakeholderRepository.delete(stakeholder);
        });
    }

    // ⭐ 關鍵修正點：removeStakeholderFromProject 僅處理聯結表關聯的解除 ⭐
    public void removeStakeholderFromProject(Long projectId, Long stakeholderId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        Stakeholder stakeholder = stakeholderRepository.findById(stakeholderId)
                .orElseThrow(() -> new ResourceNotFoundException("Stakeholder not found with id " + stakeholderId));

        // 僅從特定專案中移除利害關係人，不刪除利害關係人實體
        project.removeStakeholder(stakeholder); 
        projectRepository.save(project); 
    }
}