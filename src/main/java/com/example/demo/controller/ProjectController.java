package com.example.demo.controller;

import com.example.demo.model.entity.Project;
import com.example.demo.model.entity.User; // 導入 User 實體
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository; // 導入 UserRepository
import com.example.demo.service.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // 導入 Authentication
import org.springframework.security.core.context.SecurityContextHolder; // 導入 SecurityContextHolder
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/api/rest/project")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository; // ⭐ 注入 UserRepository ⭐

    @GetMapping // 查詢所有專案
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Project>> getAllProjects() {
        // 目前是查找所有專案。如果您未來需要根據用戶篩選，再進行修改。
        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/{id}") // 根據 ID 查詢單一專案
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Project> getProjectById(@PathVariable("id") long id) {
        Optional<Project> projectData = projectRepository.findById(id);
        return projectData.map(project -> new ResponseEntity<>(project, HttpStatus.OK))
                          .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping // 新增專案
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            // ⭐ 獲取當前登入用戶的 ID ⭐
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long currentUserId = userDetails.getId();

            // 從資料庫中獲取完整的 User 對象
            Optional<User> currentUserOptional = userRepository.findById(currentUserId);
            if (!currentUserOptional.isPresent()) {
                // 如果用戶不存在，這是一個不應該發生的錯誤（除非數據庫用戶被刪除但Token仍有效）
                System.err.println("創建專案失敗：當前登入用戶 ID " + currentUserId + " 未找到。");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            User currentUser = currentUserOptional.get();

            // ⭐ 檢查是否已經存在同名專案 (如果您的專案名稱設定為唯一) ⭐
            if (projectRepository.findByName(project.getName()).isPresent()) {
                System.err.println("創建專案失敗：專案名稱 '" + project.getName() + "' 已存在。");
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 返回 409 Conflict
            }

            // ⭐ 創建新專案並設置用戶 ⭐
            Project newProject = new Project(project.getName(), project.getDescription());
            newProject.setUser(currentUser); // ⭐ 將當前登入用戶設置給專案的 'user' 欄位 ⭐

            Project _project = projectRepository.save(newProject);
            return new ResponseEntity<>(_project, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("創建專案時發生內部錯誤: " + e.getMessage());
            e.printStackTrace(); // 打印完整的堆疊追蹤到後台控制台
            // 返回 500 錯誤，並可以考慮在生產環境中返回一個更友好的通用錯誤訊息
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}") // 修改專案
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Project> updateProject(@PathVariable("id") long id, @RequestBody Project project) {
        Optional<Project> projectData = projectRepository.findById(id);

        if (projectData.isPresent()) {
            Project _project = projectData.get();
            _project.setName(project.getName());
            _project.setDescription(project.getDescription());

            // ⭐ 安全檢查 (可選): 只有專案創建者或管理員才能修改 ⭐
            // 如果啟用這個，請確保前端也處理了 403 Forbidden
            /*
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            // 檢查是否是創建者，或者是否是 ADMIN 角色
            boolean isCreator = _project.getUser() != null && _project.getUser().getId().equals(userDetails.getId());
            boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isCreator && !isAdmin) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
            }
            */

            // 注意：user_id (創建者) 通常不應通過 PUT 請求從前端修改。
            // 這個欄位是專案創建時固定的。

            return new ResponseEntity<>(projectRepository.save(_project), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}") // 刪除專案
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteProject(@PathVariable("id") long id) {
        try {
            // ⭐ 安全檢查 (可選): 只有專案創建者或管理員才能刪除 ⭐
            // 如果啟用這個，請確保前端也處理了 403 Forbidden
            /*
            Optional<Project> projectData = projectRepository.findById(id);
            if (projectData.isPresent()) {
                Project projectToDelete = projectData.get();
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                boolean isCreator = projectToDelete.getUser() != null && projectToDelete.getUser().getId().equals(userDetails.getId());
                boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (!isCreator && !isAdmin) {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
                }
            } else {
                // 如果要刪除的專案不存在，也可以直接返回 NOT_FOUND 或 NO_CONTENT，取決於您的設計
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            */

            projectRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content 表示成功但無返回內容
        } catch (Exception e) {
            System.err.println("刪除專案時發生內部錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}