package com.example.demo.repository;

// src/main/java/com/example/demo/repository/StakeholderRepository.java

import com.example.demo.model.entity.Stakeholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StakeholderRepository extends JpaRepository<Stakeholder, Long> {
    // 根據關聯的 Project ID 查找所有 Stakeholder
    List<Stakeholder> findByProjectId(Long projectId); // 這個方法名很重要，JPA 會自動解析
}