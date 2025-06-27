package com.example.demo.service;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.model.entity.UserActivity.ActionType;
import com.example.demo.repository.UserActivityRepository;
import com.example.demo.response.UserActivityResponse;
import jakarta.servlet.http.HttpServletRequest; // 用於獲取 IP 地址
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication; // 用於獲取用戶名
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    /**
     * 記錄用戶活動
     * @param actionType 活動類型 (使用 UserActivity.ActionType 枚舉)
     * @param action 活動的簡要描述 (例如 "創建專案")
     * @param details 活動的詳細資訊 (例如 "新專案名稱: MyProject")
     */
    public void recordActivity(ActionType actionType, String action, String details) {
        String username = getCurrentUsername();
        String ipAddress = getCurrentIpAddress();

        UserActivity userActivity = new UserActivity(username, action, actionType, details, ipAddress);
        userActivityRepository.save(userActivity);
        System.out.println("ACTIVITY LOGGED: User: " + username + ", Action: " + action + ", Type: " + actionType); // 方便調試
    }

    /**
     * 獲取所有用戶活動，支持分頁和搜尋
     * @param page 頁碼 (從 0 開始)
     * @param size 每頁大小
     * @param sortBy 排序欄位 (預設為 timestamp)
     * @param sortDir 排序方向 (asc/desc)
     * @param searchTerm 搜尋關鍵字 (針對 action, username, details 欄位)
     * @param actionTypeFilter 可選的 ActionType 過濾器 (例如 "CREATE_PROJECT")
     * @return 分頁的用戶活動響應
     */
    public Page<UserActivityResponse> getAllActivities(
            int page, int size, String sortBy, String sortDir,
            String searchTerm, String actionTypeFilter) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserActivity> activitiesPage;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // 組合搜尋 action, username, details
            activitiesPage = userActivityRepository.findByActionContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrDetailsContainingIgnoreCase(
                    searchTerm, searchTerm, searchTerm, pageable);
        } else if (actionTypeFilter != null && !actionTypeFilter.trim().isEmpty()) {
            try {
                ActionType type = ActionType.valueOf(actionTypeFilter.toUpperCase());
                activitiesPage = userActivityRepository.findByActionType(type, pageable);
            } catch (IllegalArgumentException e) {
                // 如果 actionTypeFilter 無效，返回所有（或空頁）
                activitiesPage = Page.empty(pageable);
            }
        }
        else {
            activitiesPage = userActivityRepository.findAll(pageable);
        }

        return activitiesPage.map(this::convertToResponse);
    }

    /**
     * 根據 ID 獲取單個用戶活動條目
     */
    public Optional<UserActivityResponse> getActivityById(Long id) {
        return userActivityRepository.findById(id).map(this::convertToResponse);
    }

    // Helper method to convert Entity to Response DTO
    private UserActivityResponse convertToResponse(UserActivity userActivity) {
        UserActivityResponse response = new UserActivityResponse();
        response.setId(userActivity.getId());
        response.setUsername(userActivity.getUsername());
        response.setAction(userActivity.getAction());
        response.setActionType(userActivity.getActionType());
        response.setDetails(userActivity.getDetails());
        response.setTimestamp(userActivity.getTimestamp());
        response.setIpAddress(userActivity.getIpAddress());
        return response;
    }
    
    public void logUserActivity(String username, String action, ActionType actionType) {
        UserActivity activity = new UserActivity();
        activity.setUsername(username != null ? username : "anonymousUser"); // 如果沒有用戶名，設為匿名
        activity.setAction(action);
        activity.setActionType(actionType); // 使用枚舉類型
        activity.setTimestamp(LocalDateTime.now());
        userActivityRepository.save(activity);
    }

    // 從 Spring Security 上下文獲取當前用戶名
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // 返回用戶名
        }
        return "anonymous"; // 如果未認證，預設為 anonymous
    }

    // 獲取當前請求的 IP 地址
    private String getCurrentIpAddress() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(HttpServletRequest::getRemoteAddr)
                .orElse("N/A");
    }
    
 
	
}