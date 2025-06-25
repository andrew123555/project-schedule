package com.example.demo.controller;

// 導入後端服務和 DTO
import com.example.demo.payload.TodoItemRequest; // 使用獨立的 TodoItemRequest DTO
import com.example.demo.response.TodoItemResponse; // 使用獨立的 TodoItemResponse DTO
import com.example.demo.service.TodoItemService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 注意：@CrossOrigin 應優先在 WebSecurityConfig 中全局配置
// 如果您希望這個控制器有特定的 CORS 規則，可以在這裡添加，但通常不建議與全局配置衝突
// 為了演示，我們暫時保留，但建議在 WebSecurityConfig 中統一管理 CORS。
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/rest/todo-items") // 待辦事項的 API 基礎路徑
public class TodoItemController {

    // 只需要注入 TodoItemService，因為所有業務邏輯和資料庫操作都在服務層處理
    @Autowired
    private TodoItemService todoItemService;

    // 創建待辦事項
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    // 直接接收獨立的 TodoItemRequest DTO
    public ResponseEntity<TodoItemResponse> createTodoItem(@Valid @RequestBody TodoItemRequest request) {
        // 將請求直接傳遞給服務層處理
        TodoItemResponse createdTodoItem = todoItemService.createTodoItem(request);
        return new ResponseEntity<>(createdTodoItem, HttpStatus.CREATED); // 返回 201 Created
    }

    // 獲取所有待辦事項 (可選，如果您需要獲取所有待辦事項而非按專案)
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<TodoItemResponse>> getAllTodoItems() {
        // 假設服務層有這個方法
        List<TodoItemResponse> todoItems = todoItemService.getAllTodoItems();
        if (todoItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(todoItems, HttpStatus.OK); // 200 OK
    }

    // 獲取某個專案的所有待辦事項
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<TodoItemResponse>> getTodoItemsByProjectId(@PathVariable Long projectId) {
        // 將請求直接傳遞給服務層處理
        List<TodoItemResponse> todoItems = todoItemService.getTodoItemsByProjectId(projectId);
        if (todoItems.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(todoItems, HttpStatus.OK); // 200 OK
    }

    // 獲取單個待辦事項
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TodoItemResponse> getTodoItemById(@PathVariable Long id) {
        // 服務層會拋出 ResourceNotFoundException，由全局異常處理器處理為 404
        TodoItemResponse todoItem = todoItemService.getTodoItemById(id);
        return ResponseEntity.ok(todoItem); // 200 OK
    }

    // 更新待辦事項
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    // 直接接收獨立的 TodoItemRequest DTO
    public ResponseEntity<TodoItemResponse> updateTodoItem(@PathVariable Long id, @Valid @RequestBody TodoItemRequest request) {
        // 將請求直接傳遞給服務層處理
        TodoItemResponse updatedTodoItem = todoItemService.updateTodoItem(id, request);
        return ResponseEntity.ok(updatedTodoItem); // 返回 200 OK
    }

    // 刪除待辦事項
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')") // 刪除操作通常需要更高的權限
    public ResponseEntity<Void> deleteTodoItem(@PathVariable Long id) {
        // 將請求直接傳遞給服務層處理
        todoItemService.deleteTodoItem(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }

    // 您可能還需要一個端點來切換待辦事項的完成狀態，假設服務層有 toggleTodoItemCompletion 方法
    @PatchMapping("/{id}/toggle-completion")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TodoItemResponse> toggleTodoItemCompletion(@PathVariable Long id) {
        TodoItemResponse updatedTodoItem = todoItemService.toggleTodoItemCompletion(id);
        return ResponseEntity.ok(updatedTodoItem);
    }

    // 注意：已移除原先位於此處的 getAllUsers 方法，因為該功能已在 UserController 中提供。
    // 也已移除直接注入的 TodoItemRepository, ProjectRepository, UserRepository, JwtUtils，
    // 因為它們的職責已下放至服務層。
}