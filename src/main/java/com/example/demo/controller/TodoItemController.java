package com.example.demo.controller;

import com.example.demo.model.entity.TodoItem;
import com.example.demo.model.entity.User; 
import com.example.demo.model.entity.UserActivity; 
import com.example.demo.payload.TodoItemRequest;
import com.example.demo.response.TodoItemResponse;
import com.example.demo.response.UserInfoResponse; 
import com.example.demo.service.TodoItemService;
import com.example.demo.service.UserActivityService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/project/{projectId}/todo-items") // 保持路徑為 /project/{projectId}/todo-items
public class TodoItemController {

    @Autowired
    private TodoItemService todoItemService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private UserRepository userRepository; 

    @PreAuthorize("isAuthenticated()") 
    @GetMapping("/assignees")
    public ResponseEntity<List<UserInfoResponse>> getAllUsersForAssignee() {
        List<User> users = userRepository.findAll(); 
        List<UserInfoResponse> userDTOs = users.stream().map(user -> {
            return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), null, null);
        }).collect(Collectors.toList());
        userActivityService.logUserActivity(getCurrentUsername(), "查看了負責人列表",UserActivity.ActionType.api_access); 
        return ResponseEntity.ok(userDTOs);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<TodoItemResponse>> getAllTodoItems(@PathVariable Long projectId) {
        List<TodoItem> todoItems = todoItemService.getTodoItemsByProjectId(projectId);
        List<TodoItemResponse> responses = todoItems.stream()
                .map(TodoItemResponse::new)
                .collect(Collectors.toList());
        userActivityService.logUserActivity(getCurrentUsername(), "查看了專案 " + projectId + " 的所有待辦事項", UserActivity.ActionType.todo_item_management); 
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{todoItemId}")
    public ResponseEntity<TodoItemResponse> getTodoItemById(@PathVariable Long projectId, @PathVariable Long todoItemId) {
        TodoItem todoItem = todoItemService.getTodoItemById(todoItemId)
                .orElseThrow(() -> new RuntimeException("TodoItem not found with id " + todoItemId));
        userActivityService.logUserActivity(getCurrentUsername(), "查看了專案 " + projectId + " 中的待辦事項 " + todoItemId, UserActivity.ActionType.todo_item_management); 
        return ResponseEntity.ok(new TodoItemResponse(todoItem));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TodoItemResponse> createTodoItem(@PathVariable Long projectId, @Valid @RequestBody TodoItemRequest todoItemRequest) {
        TodoItem newTodoItem = todoItemService.createTodoItem(projectId, todoItemRequest);
        userActivityService.logUserActivity(getCurrentUsername(), "在專案 " + projectId + " 中創建了待辦事項: " + newTodoItem.getTitle(), UserActivity.ActionType.todo_item_management); 
        return new ResponseEntity<>(new TodoItemResponse(newTodoItem), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{todoItemId}")
    public ResponseEntity<TodoItemResponse> updateTodoItem(@PathVariable Long projectId, @PathVariable Long todoItemId, @Valid @RequestBody TodoItemRequest todoItemRequest) {
        TodoItem updatedTodoItem = todoItemService.updateTodoItem(projectId, todoItemId, todoItemRequest);
        userActivityService.logUserActivity(getCurrentUsername(), "更新了專案 " + projectId + " 中的待辦事項: " + updatedTodoItem.getTitle(), UserActivity.ActionType.todo_item_management); 
        return ResponseEntity.ok(new TodoItemResponse(updatedTodoItem));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{todoItemId}")
    public ResponseEntity<HttpStatus> deleteTodoItem(@PathVariable Long projectId, @PathVariable Long todoItemId) {
        todoItemService.deleteTodoItem(projectId, todoItemId);
        userActivityService.logUserActivity(getCurrentUsername(), "刪除了專案 " + projectId + " 中的待辦事項 " + todoItemId, UserActivity.ActionType.todo_item_management); 
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getCurrentUsername() {
        try {
            return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        } catch (Exception e) {
            System.err.println("Error getting current username in TodoItemController: " + e.getMessage());
            return "anonymousUser";
        }
    }
}