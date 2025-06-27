package com.example.demo.controller;

import com.example.demo.response.UserActivityResponse;
import com.example.demo.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/activities") // 用戶活動 API 的基礎路徑
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    /**
     * 獲取所有用戶活動條目，支持分頁和搜尋
     * GET /api/activities?page=0&size=10&sortBy=timestamp&sortDir=desc&searchTerm=project&actionTypeFilter=CREATE_PROJECT
     */
    @GetMapping
    public ResponseEntity<Page<UserActivityResponse>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String actionTypeFilter) { // 新增 ActionType 過濾器

        Page<UserActivityResponse> activitiesPage = userActivityService.getAllActivities(
                page, size, sortBy, sortDir, searchTerm, actionTypeFilter);
        return ResponseEntity.ok(activitiesPage);
    }

    /**
     * 根據 ID 獲取單個用戶活動條目
     * GET /api/activities/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserActivityResponse> getActivityById(@PathVariable Long id) {
        return userActivityService.getActivityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}