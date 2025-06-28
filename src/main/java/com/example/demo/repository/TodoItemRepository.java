package com.example.demo.repository;

import com.example.demo.model.entity.TodoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph; // 導入 EntityGraph
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // ⭐ 關鍵修正：使用 @EntityGraph 來強制加載 "project" 屬性 ⭐
    @EntityGraph(attributePaths = "project")
    Page<TodoItem> findByProjectId(Long projectId, Pageable pageable);
}