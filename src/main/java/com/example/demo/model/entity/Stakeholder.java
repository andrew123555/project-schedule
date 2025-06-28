package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode; // 導入 EqualsAndHashCode

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "stakeholders")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
// ⭐ 關鍵修正點：在 Stakeholder 實體的 @Data 註解上排除 projects 集合 ⭐
@EqualsAndHashCode(exclude = "projects") 
public class Stakeholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false) 
    private String role; 

    @Column(name = "contact_info", columnDefinition = "TEXT") 
    private String contactInfo;

    @Column(name = "phone")
    private String phone;

    @Column(name = "requirement", columnDefinition = "TEXT")
    private String requirement;

    @Column(name = "power")
    private String power; 

    @Column(name = "interest")
    private String interest; 

    @Column(name = "matrix_status")
    private String matrixStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "stakeholders", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    // Lombok 的 @Data 會自動生成 getter/setter。
    // 如果沒有 Lombok，需要手動添加所有 getter/setter。
}