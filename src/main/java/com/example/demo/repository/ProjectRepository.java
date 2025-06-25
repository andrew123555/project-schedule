package com.example.demo.repository;

import com.example.demo.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // 引入 Optional

@Repository // 標記為 Spring Bean
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // JpaRepository 已經提供了：
    List<Project> findAll(); //(用於獲取所有專案)
    Optional<Project> findById(Long id); // - (用於根據 ID 查找專案)
    Project save(Project project); // - (用於保存新專案或更新現有專案)
    void deleteById(Long id); // - (用於刪除專案)

    // 您可以根據業務需求添加自定義方法。
    // 例如，如果您想根據專案名稱查找，且名稱是唯一的：
    Optional<Project> findByName(String name);

    // 如果專案可以屬於特定用戶 (假設 Project 實體中有一個 user 字段或 user_id 字段)
    // 您可能需要根據用戶 ID 查找專案：
    List<Project> findByUserId(Long userId); // 如果 Project 實體中有 user_id 欄位

    // 如果您想根據名稱模糊查詢
    List<Project> findByNameContainingIgnoreCase(String name);
}