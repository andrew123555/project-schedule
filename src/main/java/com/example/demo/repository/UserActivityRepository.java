package com.example.demo.repository;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.model.entity.UserActivity.ActionType; // 導入 ActionType
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    // 根據行動訊息內容進行模糊搜尋
    Page<UserActivity> findByActionContainingIgnoreCase(String action, Pageable pageable);

    // 根據用戶名進行模糊搜尋
    Page<UserActivity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    // 根據詳細資訊進行模糊搜尋
    Page<UserActivity> findByDetailsContainingIgnoreCase(String details, Pageable pageable);

    // 根據 ActionType 搜尋
    Page<UserActivity> findByActionType(ActionType actionType, Pageable pageable);

    // 組合搜尋（可選，提供更複雜的查詢能力）
    Page<UserActivity> findByActionContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrDetailsContainingIgnoreCase(
            String actionSearch, String usernameSearch, String detailsSearch, Pageable pageable);
}