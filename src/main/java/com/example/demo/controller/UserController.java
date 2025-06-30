package com.example.demo.controller;

import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.payload.UserRoleUpdateRequest;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.service.UserActivityService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.model.entity.UserActivity.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserActivityService userActivityService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers(HttpServletRequest request) {
        List<User> users = userRepository.findAllWithRoles();
        List<UserInfoResponse> userDTOs = users.stream().map(user -> {
            List<String> roles = user.getRoles().stream()
                                         .map(role -> role.getName().name())
                                         .collect(Collectors.toList());
            return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), roles, null);
        }).collect(Collectors.toList());

        // ⭐ 修正點：使用 ActionType.user_view_all ⭐
        userActivityService.recordActivity(ActionType.user_view_all,
                                           "查看所有用戶", // actionDescription
                                           "查看了所有用戶列表 (共 " + users.size() + " 個用戶)", // details
                                           request.getRemoteAddr());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserRoles(@PathVariable("id") Long id,
                                             @Valid @RequestBody UserRoleUpdateRequest roleUpdateRequest,
                                             HttpServletRequest request) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            User user = userData.get();
            Set<String> strRoles = roleUpdateRequest.getRoles();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null || strRoles.isEmpty()) {
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
                roles.add(userRole);
            } else {
                strRoles.forEach(roleStr -> {
                    String normalizedRoleStr = roleStr.replace("ROLE_", "").toLowerCase();
                    
                    switch (normalizedRoleStr) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
                            roles.add(adminRole);
                            break;
                        case "moderator":
                            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                    .orElseThrow(() -> new RuntimeException("Error: Moderator Role is not found."));
                            roles.add(modRole);
                            break;
                        case "user":
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: User Role is not found."));
                            roles.add(userRole);
                            break;
                        default:
                            throw new RuntimeException("Error: Invalid role specified: " + roleStr);
                    }
                });
            }
            user.setRoles(roles);
            User updatedUser = userRepository.save(user);

            String newRoles = updatedUser.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.joining(", "));
            userActivityService.recordActivity(ActionType.user_role_update,
                                               "更新用戶角色", // actionDescription
                                               "用戶: " + updatedUser.getUsername() + ", 新角色: " + newRoles, // details
                                               request.getRemoteAddr());

            List<String> updatedRoleNames = updatedUser.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new UserInfoResponse(
                    updatedUser.getId(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    updatedRoleNames,
                    null
            ));

        } else {
            userActivityService.recordActivity(ActionType.error,
                                               "更新用戶角色失敗", 
                                               "未找到用戶 ID: " + id, 
                                               request.getRemoteAddr());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test/all")
    public String allAccess(HttpServletRequest request) {
        userActivityService.recordActivity(ActionType.api_access,
                                           "執行公共訪問測試", 
                                           "訪問了 /test/all 端點", // details
                                           request.getRemoteAddr());
        return "Public Content.";
    }

    @GetMapping("/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess(HttpServletRequest request) {
        userActivityService.recordActivity(ActionType.api_access,
                                           "執行用戶訪問測試", 
                                           "訪問了 /test/user 端點", 
                                           request.getRemoteAddr());
        return "User Content.";
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(HttpServletRequest request) {
        userActivityService.recordActivity(ActionType.api_access,
                                           "執行管理員訪問測試", 
                                           "訪問了 /test/admin 端點", 
                                           request.getRemoteAddr());
        return "Admin Board.";
    }
}