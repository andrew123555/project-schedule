package com.example.demo.service; // 注意：根據您提供的 TodoItemService 程式碼，這裡的 package 名稱是 'com.example.demo.service.impl'
                                   // 但在 Controller 中您使用了 'com.example.demo.services.TodoItemService'。
                                   // 請確認這兩者一致。如果您的 Service 實際在 com.example.demo.service.impl，
                                   // 則 Controller 中的 import 也要改成 import com.example.demo.service.impl.TodoItemService;
                                   // 我假設您希望 Service 層的 package 名稱是 com.example.demo.services，並將在此提供。

import com.example.demo.exception.ResourceNotFoundException; // 假設你有一個自定義的例外
import com.example.demo.model.entity.Project; // 假設你的 Project 實體類別路徑
import com.example.demo.model.entity.User;     // 假設你的 User 實體類別路徑
import com.example.demo.model.entity.TodoItem; // 假設你的 TodoItem 實體類別路徑
import com.example.demo.payload.TodoItemRequest;
import com.example.demo.response.TodoItemResponse;
import com.example.demo.repository.TodoItemRepository; // 假設你的 TodoItemRepository 路徑
import com.example.demo.repository.ProjectRepository;   // 假設你的 ProjectRepository 路徑
import com.example.demo.repository.UserRepository;     // 假設你的 UserRepository 路徑

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 處理事務

import java.util.List;
import java.util.stream.Collectors;

@Service // 標記為 Spring Service 組件，以便可以被 @Autowired 注入
public class TodoItemService {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private ProjectRepository projectRepository; // 需要 ProjectRepository 來驗證 Project ID

    @Autowired
    private UserRepository userRepository; // 需要 UserRepository 來查找 Assignee

    /**
     * 創建一個新的待辦事項。
     *
     * @param request 包含待辦事項資料的 TodoItemRequest DTO。
     * @return 創建成功的 TodoItemResponse DTO。
     * @throws ResourceNotFoundException 如果指定的專案或負責人不存在。
     */
    @Transactional
    public TodoItemResponse createTodoItem(TodoItemRequest request) {
        // 檢查 Project 是否存在
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + request.getProjectId()));

        // 檢查 Assignee 是否存在 (如果提供了 assigneeId)
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found with id " + request.getAssigneeId()));
        }

        // 創建 TodoItem 實體
        TodoItem todoItem = new TodoItem();
        todoItem.setName(request.getName());
        todoItem.setCategory(request.getCategory());
        todoItem.setStartTime(request.getStartTime());
        todoItem.setEndTime(request.getEndTime());
        todoItem.setProject(project); // 設定關聯的 Project
        todoItem.setAssignee(assignee); // 設定關聯的 Assignee (如果存在)
        todoItem.setConfidential(request.getConfidential());
        todoItem.setCompleted(false); // 預設為未完成

        TodoItem savedTodoItem = todoItemRepository.save(todoItem);

        return convertToTodoItemResponse(savedTodoItem);
    }

    /**
     * 獲取所有待辦事項。
     * @return 所有待辦事項的 TodoItemResponse 列表。
     */
    @Transactional(readOnly = true)
    public List<TodoItemResponse> getAllTodoItems() {
        List<TodoItem> todoItems = todoItemRepository.findAll(); // 使用 findAll() 獲取所有 TodoItem
        return todoItems.stream()
                .map(this::convertToTodoItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根據專案 ID 獲取所有待辦事項。
     *
     * @param projectId 專案的 ID。
     * @return 待辦事項的 TodoItemResponse 列表。
     * @throws ResourceNotFoundException 如果指定的專案不存在。
     */
    @Transactional(readOnly = true)
    public List<TodoItemResponse> getTodoItemsByProjectId(Long projectId) {
        // 可以選擇先檢查 Project 是否存在
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id " + projectId);
        }

        List<TodoItem> todoItems = todoItemRepository.findByProjectId(projectId); // 假設你已經在 Repository 中定義了這個方法
        return todoItems.stream()
                .map(this::convertToTodoItemResponse) // 使用輔助方法轉換
                .collect(Collectors.toList());
    }

    /**
     * 根據 ID 獲取單個待辦事項。
     *
     * @param id 待辦事項的 ID。
     * @return 對應的 TodoItemResponse DTO。
     * @throws ResourceNotFoundException 如果待辦事項不存在。
     */
    @Transactional(readOnly = true)
    public TodoItemResponse getTodoItemById(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id " + id));
        return convertToTodoItemResponse(todoItem);
    }

    /**
     * 更新一個現有的待辦事項。
     *
     * @param id 要更新的待辦事項的 ID。
     * @param request 包含更新資料的 TodoItemRequest DTO。
     * @return 更新後的 TodoItemResponse DTO。
     * @throws ResourceNotFoundException 如果待辦事項、專案或負責人不存在。
     */
    @Transactional
    public TodoItemResponse updateTodoItem(Long id, TodoItemRequest request) {
        TodoItem existingTodoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id " + id));

        // 檢查 Project 是否存在
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + request.getProjectId()));

        // 檢查 Assignee 是否存在 (如果提供了 assigneeId)
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee user not found with id " + request.getAssigneeId()));
        }

        // 更新實體的屬性
        existingTodoItem.setName(request.getName());
        existingTodoItem.setCategory(request.getCategory());
        existingTodoItem.setStartTime(request.getStartTime());
        existingTodoItem.setEndTime(request.getEndTime());
        existingTodoItem.setProject(project);
        existingTodoItem.setAssignee(assignee);
        existingTodoItem.setConfidential(request.getConfidential());
        // 如果 request 中包含了 completed 狀態，也可以在這裡更新：
        // existingTodoItem.setCompleted(request.getCompleted());

        TodoItem updatedTodoItem = todoItemRepository.save(existingTodoItem);

        return convertToTodoItemResponse(updatedTodoItem);
    }

    /**
     * 刪除一個待辦事項。
     *
     * @param id 要刪除的待辦事項的 ID。
     * @throws ResourceNotFoundException 如果待辦事項不存在。
     */
    @Transactional
    public void deleteTodoItem(Long id) {
        if (!todoItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("TodoItem not found with id " + id);
        }
        todoItemRepository.deleteById(id);
    }

    /**
     * 將 TodoItem 實體轉換為 TodoItemResponse DTO。
     *
     * @param todoItem 要轉換的 TodoItem 實體。
     * @return 轉換後的 TodoItemResponse DTO。
     */
    private TodoItemResponse convertToTodoItemResponse(TodoItem todoItem) {
        String assigneeUsername = (todoItem.getAssignee() != null) ? todoItem.getAssignee().getUsername() : null;

        // 如果 TodoItemResponse 的建構子沒有接收 completed，則需要更新 TodoItemResponse DTO 的建構子。
        // 或者使用 Builder 模式。這裡假設 TodoItemResponse 的建構子已包含 completed 參數。
        return new TodoItemResponse(
                todoItem.getId(),
                todoItem.getName(),
                todoItem.getCategory(),
                todoItem.getStartTime(),
                todoItem.getEndTime(),
                todoItem.getProject() != null ? todoItem.getProject().getId() : null, // 確保 Project 不為空
                todoItem.getAssignee() != null ? todoItem.getAssignee().getId() : null, // 確保 Assignee 不為空
                assigneeUsername,
                todoItem.getConfidential(),
                todoItem.getCompleted() // 傳遞 completed 狀態
        );
    }

    /**
     * 切換待辦事項的完成狀態。
     *
     * @param id 待辦事項的 ID。
     * @return 更新後的 TodoItemResponse DTO。
     * @throws ResourceNotFoundException 如果待辦事項不存在。
     */
    @Transactional
    public TodoItemResponse toggleTodoItemCompletion(Long id) {
        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TodoItem not found with id " + id));
        todoItem.setCompleted(!todoItem.getCompleted()); // 切換完成狀態
        TodoItem updatedTodoItem = todoItemRepository.save(todoItem);
        return convertToTodoItemResponse(updatedTodoItem);
    }
}