package com.example.demo.controller;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/api/activities")
public class UserActivityController {

    @Autowired
    private UserActivityRepository userActivityRepository;

    /**
     * 獲取所有使用者活動日誌，支援篩選。
     * 只有擁有 ROLE_ADMIN 權限的使用者才能訪問。
     *
     * @param username 可選，按使用者名稱篩選 (模糊匹配)
     * @param startDate 可選，開始日期 (yyyy-MM-dd HH:mm:ss 格式)
     * @param endDate   可選，結束日期 (yyyy-MM-dd HH:mm:ss 格式)
     * @return 包含活動日誌列表的 ResponseEntity
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 只有管理員才能查看
    public ResponseEntity<?> getAllActivities(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        List<UserActivity> activities;

        if (username != null && !username.isEmpty() && startDate != null && endDate != null) {
            // 根據使用者名稱和時間範圍篩選
            activities = userActivityRepository.findByUsernameContainingIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(username, startDate, endDate);
        } else if (username != null && !username.isEmpty()) {
            // 只根據使用者名稱篩選
            activities = userActivityRepository.findByUsernameContainingIgnoreCaseOrderByTimestampDesc(username);
        } else if (startDate != null && endDate != null) {
            // 只根據時間範圍篩選
            activities = userActivityRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
        } else {
            // 獲取所有活動
            activities = userActivityRepository.findAllByOrderByTimestampDesc();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("data", activities);
        response.put("message", "操作日誌獲取成功!");
        return ResponseEntity.ok(response);
    }
}