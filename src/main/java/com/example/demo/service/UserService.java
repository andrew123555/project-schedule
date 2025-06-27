// src/main/java/com/example/demo/service/UserService.java
package com.example.demo.service;

import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // 獲取所有用戶，確保角色被加載
    //@PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true) // 這是讀取操作
    public List<User> getAllUsers() {
        return userRepository.findAllWithRoles(); // ⭐ 使用我們新定義的查詢 ⭐
    }

    // 更新用戶角色，只有 ADMIN 可以訪問
   // @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public User updateUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        // 避免用戶沒有任何角色，至少給予 ROLE_USER
        if (roleNames == null || roleNames.isEmpty()) {
            roleNames = new HashSet<>();
            roleNames.add("user"); // 預設為 user 角色
        }

        Set<Role> newRoles = new HashSet<>();
        for (String roleName : roleNames) {
            ERole enumRoleName;
            try {
                enumRoleName = ERole.valueOf("ROLE_" + roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error: Invalid role name: " + roleName);
            }
            Role role = roleRepository.findByName(enumRoleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + enumRoleName + " not found."));
            newRoles.add(role);
        }

        user.setRoles(newRoles);
        return userRepository.save(user);
    }
    
    
}