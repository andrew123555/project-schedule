package com.example.demo.controller;

import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.payload.UserRoleUpdateRequest;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.service.UserActivityService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import org.springframework.security.core.Authentication; 
import org.springframework.security.core.context.SecurityContextHolder; 
import com.example.demo.service.UserDetailsImpl; 
import com.example.demo.model.entity.UserActivity; 

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

    protected String getCurrentUsername() { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getUsername();
            }
            return authentication.getName(); 
        }
        return "anonymousUser"; 
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        // 使用 findAllWithRoles() 來預先加載角色，這應該會獲取所有用戶
        List<User> users = userRepository.findAllWithRoles(); 
        List<UserInfoResponse> userDTOs = users.stream().map(user -> {
            List<String> roles = user.getRoles().stream()
                                     .map(role -> role.getName().name())
                                     .collect(Collectors.toList());
            return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), roles, null);
        }).collect(Collectors.toList());

        userActivityService.logUserActivity(getCurrentUsername(), "查看了所有用戶列表", UserActivity.ActionType.api_access); 
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<?> updateUserRoles(@PathVariable("id") Long id,
                                             @Valid @RequestBody UserRoleUpdateRequest roleUpdateRequest) {
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

            userActivityService.logUserActivity(updatedUser.getUsername(), 
                                               "更新了用戶 " + updatedUser.getUsername() + " 的角色為 " + 
                                               updatedUser.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.joining(", ")), 
                                               UserActivity.ActionType.user_management);

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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test/all")
    public String allAccess() {
        userActivityService.logUserActivity(getCurrentUsername(), "執行了操作: allAccess", UserActivity.ActionType.api_access);
        return "Public Content.";
    }

    @GetMapping("/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        userActivityService.logUserActivity(getCurrentUsername(), "執行了操作: userAccess", UserActivity.ActionType.api_access);
        return "User Content.";
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        userActivityService.logUserActivity(getCurrentUsername(), "執行了操作: adminAccess", UserActivity.ActionType.api_access);
        return "Admin Board.";
    }
}