package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.TodoItem;
import com.example.demo.model.entity.User; 
import com.example.demo.payload.TodoItemRequest;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TodoItemRepository;
import com.example.demo.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional // 確保服務層方法在事務中執行
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository; 

    public List<TodoItem> getAllTodoItems() {
        return todoItemRepository.findAll();
    }

    // 根據專案 ID 獲取待辦事項
    public List<TodoItem> getTodoItemsByProjectId(Long projectId) {
        List<TodoItem> todoItems = todoItemRepository.findByProjectId(projectId);
        // ⭐ 修正點：在事務內部強制初始化負責人屬性 ⭐
        // 這樣可以確保在轉換為 DTO 之前，assignee 已經被加載
        todoItems.forEach(item -> {
            if (item.getAssignee() != null) {
                // 訪問任何一個屬性即可觸發初始化
                item.getAssignee().getUsername(); 
            }
            // 如果 project 也有可能被懶加載並在 DTO 中使用，也需要類似處理
            if (item.getProject() != null) {
                item.getProject().getName(); // 觸摸 project 屬性
            }
        });
        return todoItems;
    }

    // 根據 ID 獲取待辦事項
    public Optional<TodoItem> getTodoItemById(Long id) {
        // 如果這個方法也需要負責人/專案資訊，您可能需要一個專門的預加載查詢
        // 例如：todoItemRepository.findByIdWithAssignee(id);
        return todoItemRepository.findById(id); 
    }

    // 創建待辦事項
    public TodoItem createTodoItem(Long projectId, TodoItemRequest todoItemRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));

        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(todoItemRequest.getTitle());
        todoItem.setType(todoItemRequest.getType());
        todoItem.setDescription(todoItemRequest.getDescription());
        todoItem.setStatus(TodoItem.Status.valueOf(todoItemRequest.getStatus()));
        todoItem.setPriority(todoItemRequest.getPriority());
        todoItem.setDueDate(todoItemRequest.getDueDate());
        todoItem.setCreatedAt(LocalDateTime.now());
        todoItem.setLastModifiedAt(LocalDateTime.now());
        todoItem.setProject(project);

        todoItem.setDepartment(todoItemRequest.getDepartment());

        if (todoItemRequest.getAssigneeId() != null) {
            User assignee = userRepository.findById(todoItemRequest.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id " + todoItemRequest.getAssigneeId()));
            todoItem.setAssignee(assignee);
        } else {
            todoItem.setAssignee(null); 
        }

        return todoItemRepository.save(todoItem);
    }

    // 更新待辦事項
    public TodoItem updateTodoItem(Long projectId, Long todoItemId, TodoItemRequest todoItemRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));

        return todoItemRepository.findById(todoItemId).map(todoItem -> {
            if (!todoItem.getProject().getId().equals(projectId)) {
                throw new ResourceNotFoundException("TodoItem does not belong to Project with id " + projectId);
            }
            todoItem.setTitle(todoItemRequest.getTitle());
            todoItem.setType(todoItemRequest.getType());
            todoItem.setDescription(todoItemRequest.getDescription());
            todoItem.setStatus(TodoItem.Status.valueOf(todoItemRequest.getStatus()));
            todoItem.setPriority(todoItemRequest.getPriority());
            todoItem.setDueDate(todoItemRequest.getDueDate());
            todoItem.setLastModifiedAt(LocalDateTime.now());

            todoItem.setDepartment(todoItemRequest.getDepartment());

            if (todoItemRequest.getAssigneeId() != null) {
                User assignee = userRepository.findById(todoItemRequest.getAssigneeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assignee not found with id " + todoItemRequest.getAssigneeId()));
                todoItem.setAssignee(assignee);
            } else {
                todoItem.setAssignee(null); 
            }

            return todoItemRepository.save(todoItem);
        }).orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id " + todoItemId));
    }

    // 刪除待辦事項
    public void deleteTodoItem(Long projectId, Long todoItemId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id " + projectId);
        }
        todoItemRepository.findById(todoItemId).map(todoItem -> {
            if (!todoItem.getProject().getId().equals(projectId)) {
                throw new ResourceNotFoundException("TodoItem does not belong to Project with id " + projectId);
            }
            todoItemRepository.delete(todoItem);
            return true;
        }).orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id " + todoItemId));
    }
}