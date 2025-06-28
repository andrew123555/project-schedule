package com.example.demo.service;

import com.example.demo.model.entity.TodoItem;
import com.example.demo.model.entity.Project;
import com.example.demo.payload.TodoItemRequest;
import com.example.demo.repository.TodoItemRepository;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 導入 Transactional

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // ⭐ 關鍵修正：添加 @Transactional(readOnly = true) ⭐
    @Transactional(readOnly = true)
    public Page<TodoItem> getTodoItemsByProjectId(Long projectId, Pageable pageable) {
        // 當執行 findByProjectId 時，@EntityGraph 會確保 Project 被加載
        return todoItemRepository.findByProjectId(projectId, pageable);
    }

    @Transactional(readOnly = true) // 對於讀取單個待辦事項也添加事務
    public Optional<TodoItem> getTodoItemById(Long id) {
        return todoItemRepository.findById(id);
    }

    @Transactional // 對於寫操作添加事務
    public TodoItem createTodoItem(Long projectId, TodoItemRequest todoItemRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id " + projectId));

        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(todoItemRequest.getTitle());
        todoItem.setDescription(todoItemRequest.getDescription());

        try {
            todoItem.setStatus(TodoItem.Status.valueOf(todoItemRequest.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的待辦事項狀態: " + todoItemRequest.getStatus());
        }

        try {
            todoItem.setPriority(TodoItem.Priority.valueOf(todoItemRequest.getPriority()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的待辦事項優先級: " + todoItemRequest.getPriority());
        }
        
        todoItem.setDueDate(todoItemRequest.getDueDate());
        todoItem.setProject(project);
        todoItem.setCreatedAt(LocalDateTime.now());
        todoItem.setUpdatedAt(LocalDateTime.now());
        // 處理 assignedTo (如果需要)
        return todoItemRepository.save(todoItem);
    }

    @Transactional // 對於寫操作添加事務
    public TodoItem updateTodoItem(Long projectId, Long todoItemId, TodoItemRequest todoItemRequest) {
        TodoItem existingTodoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new RuntimeException("TodoItem not found with id " + todoItemId));

        if (!existingTodoItem.getProject().getId().equals(projectId)) {
            throw new RuntimeException("TodoItem with ID " + todoItemId + " does not belong to Project with ID " + projectId);
        }

        existingTodoItem.setTitle(todoItemRequest.getTitle());
        existingTodoItem.setDescription(todoItemRequest.getDescription());

        try {
            existingTodoItem.setStatus(TodoItem.Status.valueOf(todoItemRequest.getStatus()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的待辦事項狀態: " + todoItemRequest.getStatus());
        }

        try {
            existingTodoItem.setPriority(TodoItem.Priority.valueOf(todoItemRequest.getPriority()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("無效的待辦事項優先級: " + todoItemRequest.getPriority());
        }

        existingTodoItem.setDueDate(todoItemRequest.getDueDate());
        existingTodoItem.setUpdatedAt(LocalDateTime.now());
        // 處理 assignedTo 更新 (如果需要)
        return todoItemRepository.save(existingTodoItem);
    }

    @Transactional // 對於寫操作添加事務
    public void deleteTodoItem(Long projectId, Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new RuntimeException("TodoItem not found with id " + todoItemId));

        if (!todoItem.getProject().getId().equals(projectId)) {
            throw new RuntimeException("TodoItem with ID " + todoItemId + " does not belong to Project with ID " + projectId);
        }
        todoItemRepository.delete(todoItem);
    }
}