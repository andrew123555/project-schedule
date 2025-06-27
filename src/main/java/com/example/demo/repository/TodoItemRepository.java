package com.example.demo.repository;

import com.example.demo.model.entity.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    // 根據專案 ID 查找所有待辦事項
    List<TodoItem> findByProjectId(Long projectId);

    // 根據待辦事項 ID 和專案 ID 查找特定的待辦事項
    Optional<TodoItem> findByIdAndProjectId(Long id, Long projectId);
}