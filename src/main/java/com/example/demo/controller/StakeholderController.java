package com.example.demo.controller;

import com.example.demo.model.entity.Stakeholder;
import com.example.demo.model.entity.UserActivity;
import com.example.demo.payload.StakeholderRequest; 
import com.example.demo.response.MessageResponse;
import com.example.demo.response.StakeholderResponse;
import com.example.demo.service.StakeholderService;
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
@RequestMapping("/project/{projectId}/stakeholders") 
public class StakeholderController {

    @Autowired
    private StakeholderService stakeholderService;

    @Autowired
    private UserActivityService userActivityService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')") // 重新啟用
    public ResponseEntity<List<StakeholderResponse>> getStakeholdersByProjectId(
            @PathVariable Long projectId, 
            HttpServletRequest request) {
        userActivityService.recordActivity(
            UserActivity.ActionType.api_access,
            "查看專案下的利害關係人",
            "查看了專案 ID: " + projectId + " 的利害關係人列表",
            request.getRemoteAddr()
        );
        List<StakeholderResponse> stakeholders = stakeholderService.getStakeholdersByProjectId(projectId);
        return ResponseEntity.ok(stakeholders);
    }

    @GetMapping("/{stakeholderId}") 
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')") // 重新啟用
    public ResponseEntity<StakeholderResponse> getStakeholderById(
            @PathVariable Long projectId, 
            @PathVariable Long stakeholderId, 
            HttpServletRequest request) {
        userActivityService.recordActivity(
            UserActivity.ActionType.api_access,
            "查看單個利害關係人",
            "查看了專案 ID: " + projectId + " 下的利害關係人 ID: " + stakeholderId,
            request.getRemoteAddr()
        );
        return stakeholderService.getStakeholderById(projectId, stakeholderId) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/{userId}") 
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')") // 重新啟用
    public ResponseEntity<StakeholderResponse> createStakeholder(
            @PathVariable Long projectId, 
            @PathVariable Long userId,
            @Valid @RequestBody StakeholderRequest stakeholderRequest, 
            HttpServletRequest request) {
        
        Stakeholder newStakeholder = new Stakeholder();
        newStakeholder.setName(stakeholderRequest.getName());
        newStakeholder.setEmail(stakeholderRequest.getEmail());
        newStakeholder.setPhone(stakeholderRequest.getPhone());
        newStakeholder.setRequirement(stakeholderRequest.getRequirement());
        newStakeholder.setPower(stakeholderRequest.getPower());
        newStakeholder.setInterest(stakeholderRequest.getInterest());
        newStakeholder.setMatrixStatus(stakeholderRequest.getMatrixStatus());
        newStakeholder.setRole(stakeholderRequest.getRole()); 

        StakeholderResponse createdStakeholder = stakeholderService.createStakeholder(projectId, newStakeholder, userId);
        userActivityService.recordActivity(
            UserActivity.ActionType.stakeholder_create,
            "創建利害關係人",
            "創建了利害關係人: " + createdStakeholder.getName() + " 並關聯到專案 ID: " + projectId,
            request.getRemoteAddr()
        );
        return new ResponseEntity<>(createdStakeholder, HttpStatus.CREATED);
    }

    @PutMapping("/{stakeholderId}") 
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')") // 重新啟用
    public ResponseEntity<StakeholderResponse> updateStakeholder(
            @PathVariable Long projectId, 
            @PathVariable Long stakeholderId, 
            @Valid @RequestBody StakeholderRequest stakeholderRequest, 
            HttpServletRequest request) {
        
        Stakeholder updatedStakeholderDetails = new Stakeholder();
        updatedStakeholderDetails.setName(stakeholderRequest.getName());
        updatedStakeholderDetails.setEmail(stakeholderRequest.getEmail());
        updatedStakeholderDetails.setPhone(stakeholderRequest.getPhone());
        updatedStakeholderDetails.setRequirement(stakeholderRequest.getRequirement());
        updatedStakeholderDetails.setPower(stakeholderRequest.getPower());
        updatedStakeholderDetails.setInterest(stakeholderRequest.getInterest());
        updatedStakeholderDetails.setMatrixStatus(stakeholderRequest.getMatrixStatus());
        updatedStakeholderDetails.setRole(stakeholderRequest.getRole()); 

        StakeholderResponse updatedStakeholder = stakeholderService.updateStakeholder(stakeholderId, updatedStakeholderDetails);
        userActivityService.recordActivity(
            UserActivity.ActionType.stakeholder_update,
            "更新利害關係人",
            "更新了專案 ID: " + projectId + " 下的利害關係人 ID: " + stakeholderId + " (名稱: " + updatedStakeholder.getName() + ")",
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(updatedStakeholder);
    }

    @DeleteMapping("/{stakeholderId}") 
    @PreAuthorize("hasAnyRole('ADMIN')") // 重新啟用
    public ResponseEntity<MessageResponse> deleteStakeholder(
            @PathVariable Long projectId, 
            @PathVariable Long stakeholderId, 
            HttpServletRequest request) {
        
        stakeholderService.deleteStakeholder(stakeholderId); 
        userActivityService.recordActivity(
            UserActivity.ActionType.stakeholder_delete,
            "刪除利害關係人",
            "刪除了專案 ID: " + projectId + " 下的利害關係人 ID: " + stakeholderId,
            request.getRemoteAddr()
        );
        return ResponseEntity.ok(new MessageResponse("利害關係人刪除成功！"));
    }
}