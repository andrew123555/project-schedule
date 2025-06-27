package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data; // 使用 Lombok 自動生成 getter/setter/equals/hashCode/toString
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "stakeholders") // 確保表格名稱是正確的
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Stakeholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String email;
    private String requirement;
    private boolean power;
    private boolean interest;

    @Column(name = "matrix_status") // 如果資料庫欄位名不同，請指定
    private String matrixStatus; // 使用 camelCase 與 Java 命名習慣一致

    @Column(name = "project_id", nullable = false)
    private Long projectId; // 用於關聯專案

    // 如果沒有使用 Lombok，你需要手動添加 getter/setter 和 constructors
    // 例如:
    // public Long getId() { return id; }
    // public void setId(Long id) { this.id = id; }
    // ...
}