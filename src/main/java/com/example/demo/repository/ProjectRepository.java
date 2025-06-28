package com.example.demo.repository;

import com.example.demo.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph; // 導入 EntityGraph
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // ⭐ 關鍵修正：使用 @EntityGraph 來強制加載 "user" 和 "stakeholders" 屬性 ⭐
    // 這會確保在查詢 Project 時，相關聯的 User 和 Stakeholder 數據也被立即加載，
    // 避免 LazyInitializationException。
    @EntityGraph(attributePaths = {"user", "stakeholders"})
    Optional<Project> findById(Long id);
}
