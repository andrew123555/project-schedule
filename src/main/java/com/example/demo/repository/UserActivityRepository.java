package com.example.demo.repository;

import com.example.demo.model.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    // 根據使用者名稱查找活動
    List<UserActivity> findByUsernameContainingIgnoreCaseOrderByTimestampDesc(String username);

    // 根據時間範圍查找活動
    List<UserActivity> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);

    // 根據使用者名稱和時間範圍查找活動
    List<UserActivity> findByUsernameContainingIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(String username, LocalDateTime startTime, LocalDateTime endTime);

    // 獲取所有活動 (按照時間倒序)
    List<UserActivity> findAllByOrderByTimestampDesc();
}