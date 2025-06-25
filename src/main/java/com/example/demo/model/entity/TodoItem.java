package com.example.demo.model.entity; // 確認此套件路徑與您的檔案實際位置相符

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "todo_items") // 建議的表名
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 待辦事項序號 (主鍵)

    @Column(nullable = false)
    private String name; // 待辦事項名稱

    @Column(nullable = false)
    private String category; // 類別 (例如：開發、測試、設計、會議)

    @Column(nullable = false)
    private Instant startTime; // 開始時間

    @Column(nullable = false)
    private Instant endTime; // 結束時間

    @Column(nullable = false)
    private Boolean confidential; // 機密性 (使用 Boolean)

    @Column(nullable = false) // 如果您確保在創建時總是給定一個 boolean 值 (例如 false)，則可以保持 nullable = false
    private Boolean completed; // 完成狀態

    // 與 Project 的 Many-to-One 關聯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false) // 外鍵列名，Project 是必須的
    @JsonIgnore // 防止循環引用和 LazyInitializationException
    private Project project;

    // 與 User 的 Many-to-One 關聯 (負責人)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id") // 負責人外鍵列名，如果允許為 null，則不加 nullable = false
                                      // 如果 assigneeId 在 TodoItemRequest 中可以為 null，這裡應允許 null
    @JsonIgnore // 防止循環引用和 LazyInitializationException
    private User assignee; // 負責人 (關聯到 User 實體)

    // Constructors
    public TodoItem() {}

    public TodoItem(String name, String category, Instant startTime, Instant endTime,
                    Boolean confidential, Boolean completed, Project project, User assignee) {
        this.name = name;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidential = confidential;
        this.completed = completed; // 初始化 completed 欄位
        this.project = project;
        this.assignee = assignee;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public Boolean getConfidential() { return confidential; }
    public void setConfidential(Boolean confidential) { this.confidential = confidential; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }

    // --- 建議添加 toString() 方法用於除錯 ---
    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", confidential=" + confidential +
                ", completed=" + completed +
                ", projectId=" + (project != null ? project.getId() : "null") + // 避免 LazyInitializationException
                ", assigneeId=" + (assignee != null ? assignee.getId() : "null") + // 避免 LazyInitializationException
                '}';
    }
}