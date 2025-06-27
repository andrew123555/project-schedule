package com.example.demo.repository;
// src/main/java/com/example/demo/repository/UserRepository.java (建立此檔案)

import com.example.demo.model.entity.User; // 假設您的 User 實體路徑
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email); // 根據電子郵件查找用戶

    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    List<User> findAllWithRoles();
    
   
}