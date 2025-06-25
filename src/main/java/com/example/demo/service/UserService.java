package com.example.demo.service;

//src/main/java/com/example/demo/services/UserService.java (建立此檔案)

import com.example.demo.model.entity.User; // 假設您的 User 實體路徑
import com.example.demo.repository.UserRepository; // 假設您的 UserRepository 路徑
import com.example.demo.response.UserInfoResponse; // 假設您有 UserInfoResponse DTO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

 @Autowired
 private UserRepository userRepository; // 如果您沒有 UserRepository，則需要建立它

 // 獲取所有用戶的方法，通常用於管理或選擇目的
 @Transactional(readOnly = true)
 public List<UserInfoResponse> getAllUsers() {
     List<User> users = userRepository.findAll();
     return users.stream()
             .map(this::convertToUserInfoResponse)
             .collect(Collectors.toList());
 }

 // 輔助方法：將 User 實體轉換為 UserInfoResponse DTO
 private UserInfoResponse convertToUserInfoResponse(User user) {
     // 如果這個 UserInfoResponse DTO 不存在，您需要建立它。
     // 它應該包含基本的用戶資訊（id、username、email）以及可能的角色。
     // 範例：new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles());
     return new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList()));
 }

 // 您可能還需要像 getUserById、updateUser、deleteUser 等方法。
}
