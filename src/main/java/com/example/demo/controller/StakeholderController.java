package com.example.demo.controller;

import com.example.demo.payload.StakeholderRequest;
import com.example.demo.response.StakeholderResponse;
import com.example.demo.service.StakeholderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
// ⭐ 調整基礎路徑以包含 projectId ⭐
@RequestMapping("/project/{projectId}/stakeholders")
public class StakeholderController {

    @Autowired
    private StakeholderService stakeholderService;

    // POST /projects/{projectId}/stakeholders
    // 新增利害關係人到指定專案
    @PostMapping
    public ResponseEntity<StakeholderResponse> createStakeholder(
            @PathVariable Long projectId, // 從路徑中獲取 projectId
            @RequestBody StakeholderRequest stakeholderRequest) {
        // 確保 DTO 中的 projectId 與路徑中的 projectId 一致 (可選，但建議)
        if (!stakeholderRequest.getProjectId().equals(projectId)) {
            // 或者拋出一個自定義異常，指示請求數據不一致
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        StakeholderResponse newStakeholder = stakeholderService.createStakeholder(stakeholderRequest);
        return new ResponseEntity<>(newStakeholder, HttpStatus.CREATED);
    }

    // GET /projects/{projectId}/stakeholders
    // 獲取指定專案的所有利害關係人
    @GetMapping
    public ResponseEntity<List<StakeholderResponse>> getStakeholdersByProjectId(
            @PathVariable Long projectId) {
        // ⭐ 不需要額外的 /project/{projectId} 了，因為基礎路徑已經包含了 ⭐
        List<StakeholderResponse> stakeholders = stakeholderService.getStakeholdersByProjectId(projectId);
        return ResponseEntity.ok(stakeholders);
    }

    // GET /projects/{projectId}/stakeholders/{id}
    // 獲取指定專案中特定 ID 的利害關係人
    @GetMapping("/{id}")
    public ResponseEntity<StakeholderResponse> getStakeholderById(
            @PathVariable Long projectId, // 確保路徑中包含 projectId
            @PathVariable Long id) {
        // 這裡可以選擇性地添加邏輯，驗證該 stakeholder 是否真的屬於此 projectId
        // StakeholderResponse stakeholder = stakeholderService.getStakeholderByIdAndProjectId(id, projectId);
        // 但目前先沿用現有的，它會根據 ID 查找，然後在服務層處理邏輯
        StakeholderResponse stakeholder = stakeholderService.getStakeholderById(id);
        // 如果需要進一步驗證，可以在服務層或這裡添加：
        // if (!stakeholder.getProjectId().equals(projectId)) {
        //     throw new ResourceNotFoundException("Stakeholder not found in this project");
        // }
        return ResponseEntity.ok(stakeholder);
    }

    // PUT /projects/{projectId}/stakeholders/{id}
    // 更新指定專案中特定 ID 的利害關係人
    @PutMapping("/{id}")
    public ResponseEntity<StakeholderResponse> updateStakeholder(
            @PathVariable Long projectId, // 確保路徑中包含 projectId
            @PathVariable Long id,
            @RequestBody StakeholderRequest stakeholderRequest) {
        // 確保 DTO 中的 projectId 與路徑中的 projectId 一致 (可選，但建議)
        if (!stakeholderRequest.getProjectId().equals(projectId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        StakeholderResponse updatedStakeholder = stakeholderService.updateStakeholder(id, stakeholderRequest);
        return ResponseEntity.ok(updatedStakeholder);
    }

    // DELETE /projects/{projectId}/stakeholders/{id}
    // 刪除指定專案中特定 ID 的利害關係人
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStakeholder(
            @PathVariable Long projectId, // 確保路徑中包含 projectId
            @PathVariable Long id) {
        // 這裡同樣可以選擇性地添加邏輯，驗證該 stakeholder 是否真的屬於此 projectId
        stakeholderService.deleteStakeholder(id);
        return ResponseEntity.noContent().build();
    }
}