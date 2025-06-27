package com.example.demo.response;

import com.example.demo.model.entity.UserActivity.ActionType; // 導入 ActionType
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {
    private Long id;
    private String username;
    private String action; // 新增：操作的簡要描述
    private ActionType actionType; // 新增：操作類型
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
}