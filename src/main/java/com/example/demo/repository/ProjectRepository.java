package com.example.demo.repository;

import com.example.demo.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(Long userId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.user u WHERE p.id = :id")
    Optional<Project> findById(@org.springframework.data.repository.query.Param("id") Long id);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.user")
    List<Project> findAll(); 
}