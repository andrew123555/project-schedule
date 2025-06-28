package com.example.demo.repository;

import com.example.demo.model.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ⭐ 新增：導入 JpaSpecificationExecutor ⭐
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long>, JpaSpecificationExecutor<UserActivity> {
    // 您現有的查詢方法
    // 例如：
    // List<UserActivity> findByActionContainingIgnoreCaseOrActionTypeCodeContainingIgnoreCaseOrActivityCategoryContainingIgnoreCaseOrDetailsContainingIgnoreCaseOrIpAddressContainingIgnoreCase(
    //     String action, String actionTypeCode, String activityCategory, String details, String ipAddress);

    // 由於我們將使用 Specification，可以移除舊的複雜查詢方法，如果它們不再需要。
    // 如果您需要特定的查詢，可以保留或添加。
}