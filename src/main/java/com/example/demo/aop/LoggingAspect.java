package com.example.demo.aop;

import com.example.demo.service.UserActivityService; // ⭐ 更新為 UserActivityService ⭐
import com.example.demo.model.entity.UserActivity.ActionType; // ⭐ 導入 ActionType ⭐
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // 用於獲取用戶名
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private UserActivityService userActivityService; // ⭐ 注入 UserActivityService ⭐

    @Pointcut("within(com.example.demo.controller..*)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String username = getCurrentUsername();
        String action = "執行了操作: " + methodName; // 簡要行動描述
        ActionType actionType = ActionType.api_access; // 預設類型

        // 嘗試從方法名稱推斷 ActionType
        if (methodName.startsWith("create")) {
            actionType = determineActionType(className, "CREATE");
            action = "創建了新項目";
        } else if (methodName.startsWith("update")) {
            actionType = determineActionType(className, "UPDATE");
            action = "更新了現有項目";
        } else if (methodName.startsWith("delete")) {
            actionType = determineActionType(className, "DELETE");
            action = "刪除了項目";
        } else if (methodName.startsWith("get")) {
            actionType = determineActionType(className, "VIEW");
            action = "查看了項目";
        } else if (methodName.equals("registerUser")) { // 假設您的註冊方法名
            actionType = ActionType.register;
            action = "用戶註冊";
        } else if (methodName.equals("authenticateUser")) { // 假設您的登入方法名
            actionType = ActionType.login_success;
            action = "用戶登入成功";
        } else if (methodName.equals("logoutUser")) { // 假設您的登出方法名
            actionType = ActionType.logout;
            action = "用戶登出";
        }

        String details = "方法: " + className + "." + methodName + ", 參數: " + Arrays.toString(joinPoint.getArgs());
        if (result != null && result.getClass().getName().contains("Response")) {
             try {
                 Long entityId = (Long) result.getClass().getMethod("getId").invoke(result);
                 details += ", 影響實體ID: " + entityId;
             } catch (Exception e) { /* ignore */ }
        }

        userActivityService.recordActivity(actionType, action, details);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String username = getCurrentUsername();
        String action = "操作失敗: " + methodName;
        ActionType actionType = ActionType.api_access; // 預設為 API 存取錯誤

        if (methodName.equals("authenticateUser")) {
             actionType = ActionType.login_failed;
             action = "用戶登入失敗";
        } else {
             actionType = determineActionType(className, "ERROR");
        }


        String details = "方法: " + className + "." + methodName + ", 異常: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        if (joinPoint.getArgs() != null) {
            details += ", 參數: " + Arrays.toString(joinPoint.getArgs());
        }

        userActivityService.recordActivity(actionType, action, details);
    }

    // 輔助方法：根據控制器類名和操作類型推斷更具體的 ActionType
    private ActionType determineActionType(String className, String operation) {
        if (className.contains("ProjectController")) {
            if ("CREATE".equals(operation)) return ActionType.create_reoject;
            if ("UPDATE".equals(operation)) return ActionType.update_project;
            if ("DELETE".equals(operation)) return ActionType.delete_project;
            if ("VIEW".equals(operation)) return ActionType.view_all_projects;
        } else if (className.contains("StakeholderController")) { // ⭐ 新增對 StakeholderController 的處理 ⭐
            if ("CREATE".equals(operation)) return ActionType.create_stakeholder;
            if ("UPDATE".equals(operation)) return ActionType.update_stakeholder;
            if ("DELETE".equals(operation)) return ActionType.delete_stakeholder;
            if ("VIEW".equals(operation)) return ActionType.view_stakeholders;
        }
        // ... 可以為其他控制器添加更多邏輯
        return ActionType.api_access; // 預設
    }

    // 從 Spring Security 上下文獲取當前用戶名
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // 返回用戶名
        }
        return "anonymous"; // 如果未認證，預設為 anonymous
    }
}