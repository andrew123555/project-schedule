package com.example.demo.controller;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.response.MessageResponse;
import com.example.demo.service.UserActivityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize; // 暫時移除
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RequestMapping("/activities") 
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')") // 暫時移除
    public ResponseEntity<Page<UserActivity>> getUserActivities(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) String actionDescription,
            @RequestParam(required = false) String details,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestamp,desc") String[] sort,
            HttpServletRequest request) {

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                startDateTime = LocalDateTime.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                endDateTime = LocalDateTime.parse(endDate);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        Page<UserActivity> activities = userActivityService.searchActivities(
                username, actionType, actionDescription, details, ipAddress,
                startDateTime, endDateTime, pageable);

        userActivityService.recordActivity(
                UserActivity.ActionType.api_access,
                "查看用戶活動日誌",
                "搜尋用戶活動日誌，查詢條件：用戶名=" + username + ", 動作類型=" + actionType + ", IP=" + ipAddress + ", 日期範圍=" + startDate + " - " + endDate,
                request.getRemoteAddr()
        );
        return ResponseEntity.ok(activities);
    }

    @DeleteMapping
    // @PreAuthorize("hasRole('ADMIN')") // 暫時移除
    public ResponseEntity<MessageResponse> deleteAllActivities(HttpServletRequest request) {
        userActivityService.deleteAllActivities();
        userActivityService.recordActivity(
                UserActivity.ActionType.admin_action,
                "刪除所有用戶活動日誌",
                "管理員刪除了所有用戶活動日誌",
                request.getRemoteAddr()
        );
        return ResponseEntity.ok(new MessageResponse("所有用戶活動日誌已成功刪除！"));
    }
}