package com.example.demo.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore; // 導入 @JsonIgnore

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "name")
        })
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String name;

    private String description;

    @SuppressWarnings("unused") // 如果編譯器抱怨 status 欄位未被直接使用，可以保留此註解
    private String status; // <--- 確保這個欄位存在！

    @ManyToOne(fetch = FetchType.LAZY) // 延遲載入
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // 避免在序列化 Project 時出現循環引用和 LazyInitializationException
    private User user; // 假設 User 實體在 com.example.demo.model.entity 包中

    // ⭐ 新增：One-to-Many 關聯到 TodoItem ⭐
    // mappedBy 指向 TodoItem 實體中 Project 的屬性名
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // 使用 @JsonIgnore 避免在序列化 Project 時出現循環引用和 LazyInitializationException
    // 如果您需要返回 Project 時包含其下的 TodoItem 概要，請使用 DTO
    @JsonIgnore
    private Set<TodoItem> todoItems = new HashSet<>();

    // ... 構造函數
    public Project() {
    }

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Project(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    // ⭐ 補充：包含 status 的構造函數 ⭐
    public Project(String name, String description, String status, User user) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.user = user;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ⭐ 新增：status 的 Getter 和 Setter ⭐
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // 請注意，您之前給的 User 實體在 com.example.demo.model 包下。
    // 如果您的 User 實體現在也在 com.example.demo.model.entity 包下，那麼這裡的 User 類型是正確的。
    // 如果 User 仍在 com.example.demo.model，那麼您需要調整 import 語句或將 User 移動到 entity 包。
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ⭐ 新增：todoItems 的 Getter 和 Setter ⭐
    public Set<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(Set<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    // ⭐ 新增：輔助方法，方便管理關聯關係 ⭐
    // 在 Project 中添加 TodoItem 時，同時設置 TodoItem 的 project 屬性
    public void addTodoItem(TodoItem todoItem) {
        this.todoItems.add(todoItem);
        todoItem.setProject(this); // 重要：建立雙向關聯
    }

    // 在 Project 中移除 TodoItem 時，同時解除 TodoItem 的 project 關聯
    public void removeTodoItem(TodoItem todoItem) {
        this.todoItems.remove(todoItem);
        todoItem.setProject(null); // 重要：解除雙向關聯
    }
}