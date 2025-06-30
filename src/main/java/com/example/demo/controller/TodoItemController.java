package com.example.demo.controller;

import com.example.demo.model.entity.TodoItem;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.UserActivity.ActionType; // 確保這個導入正確
import com.example.demo.payload.TodoItemRequest;
import com.example.demo.response.TodoItemResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.service.TodoItemService;
import com.example.demo.service.UserActivityService; // 確保這個導入正確
import com.example.demo.service.UserDetailsImpl; // 確保這個導入正確
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional; // ⭐ 導入 Transactional ⭐
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/project/{projectId}/todo-items")
public class TodoItemController {

    @Autowired
    private TodoItemService todoItemService;

    @Autowired
    private UserActivityService userActivityService; 

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/assignees")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") 
    @Transactional(readOnly = true) 
    public ResponseEntity<List<UserInfoResponse>> getAllUsersForAssignee(HttpServletRequest request) {
        List<User> users = userRepository.findAll();
        List<UserInfoResponse> userDTOs = users.stream().map(user -> {
            return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), null, null);
        }).collect(Collectors.toList());
        userActivityService.recordActivity(ActionType.api_access,
                                           "查看負責人列表",
                                           "獲取所有用戶作為待辦事項負責人選項",
                                           request.getRemoteAddr());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") 
    @Transactional(readOnly = true) 
    public ResponseEntity<Page<TodoItemResponse>> getAllTodoItems(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            HttpServletRequest request) {

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<TodoItem> todoItemsPage = todoItemService.getTodoItemsByProjectId(projectId, pageable);
        Page<TodoItemResponse> responsesPage = todoItemsPage.map(TodoItemResponse::new);

        userActivityService.recordActivity(ActionType.todo_view,
                                           "查看待辦事項列表",
                                           "專案 ID: " + projectId + " 的所有待辦事項 (分頁)",
                                           request.getRemoteAddr());
        return ResponseEntity.ok(responsesPage);
    }

    @GetMapping("/{todoItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") 
    @Transactional(readOnly = true) // ⭐ 關鍵修正：添加事務註解 ⭐
    public ResponseEntity<TodoItemResponse> getTodoItemById(@PathVariable Long projectId, @PathVariable Long todoItemId, HttpServletRequest request) {
        TodoItem todoItem = todoItemService.getTodoItemById(todoItemId)
                .orElseThrow(() -> new RuntimeException("TodoItem not found with id " + todoItemId));
        userActivityService.recordActivity(ActionType.todo_view,
                                           "查看單個待辦事項",
                                           "專案 ID: " + projectId + ", 待辦事項 ID: " + todoItemId,
                                           request.getRemoteAddr());
        return ResponseEntity.ok(new TodoItemResponse(todoItem));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") 
    @Transactional 
    public ResponseEntity<TodoItemResponse> createTodoItem(@PathVariable Long projectId, @Valid @RequestBody TodoItemRequest todoItemRequest, HttpServletRequest request) {
        TodoItem newTodoItem = todoItemService.createTodoItem(projectId, todoItemRequest);
        userActivityService.recordActivity(ActionType.todo_create,
                                           "創建待辦事項",
                                           "專案 ID: " + projectId + ", 待辦事項名稱: " + newTodoItem.getTitle(),
                                           request.getRemoteAddr());
        return new ResponseEntity<>(new TodoItemResponse(newTodoItem), HttpStatus.CREATED);
    }

    @PutMapping("/{todoItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") 
    @Transactional 
    public ResponseEntity<TodoItemResponse> updateTodoItem(@PathVariable Long projectId, @PathVariable Long todoItemId, @Valid @RequestBody TodoItemRequest todoItemRequest, HttpServletRequest request) {
        TodoItem updatedTodoItem = todoItemService.updateTodoItem(projectId, todoItemId, todoItemRequest);
        userActivityService.recordActivity(ActionType.todo_update,
                                           "更新待辦事項",
                                           "專案 ID: " + projectId + ", 待辦事項名稱: " + updatedTodoItem.getTitle(),
                                           request.getRemoteAddr());
        return ResponseEntity.ok(new TodoItemResponse(updatedTodoItem));
    }

    @DeleteMapping("/{todoItemId}")
    @PreAuthorize("hasRole('ADMIN')") 
    @Transactional 
    public ResponseEntity<HttpStatus> deleteTodoItem(@PathVariable Long projectId, @PathVariable Long todoItemId, HttpServletRequest request) {
        try {
            todoItemService.deleteTodoItem(projectId, todoItemId);
            userActivityService.recordActivity(ActionType.todo_delete,
                                               "刪除待辦事項",
                                               "專案 ID: " + projectId + ", 待辦事項 ID: " + todoItemId,
                                               request.getRemoteAddr());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("刪除待辦事項時發生內部錯誤: " + e.getMessage());
            e.printStackTrace();
            userActivityService.recordActivity(ActionType.error,
                                               "刪除待辦事項失敗",
                                               "專案 ID: " + projectId + ", 待辦事項 ID: " + todoItemId + ", 錯誤: " + e.getMessage(),
                                               request.getRemoteAddr());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}