package com.example.demo.repository;

import com.example.demo.model.entity.Stakeholder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// No specific findByProjectId or findByProjectIdAndUserId methods here
// as the primary way to get stakeholders for a project will be through the Project entity
// or by using @Query if direct repository access is needed for complex queries.
@Repository
public interface StakeholderRepository extends JpaRepository<Stakeholder, Long> {
    // You can add custom query methods here if needed, e.g.,
    // List<Stakeholder> findByUserId(Long userId); // If you want to find stakeholders by their creator
}