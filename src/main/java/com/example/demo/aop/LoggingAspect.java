package com.example.demo.aop;

import com.example.demo.service.UserActivityService;
import com.example.demo.model.entity.UserActivity.ActionType;
import com.example.demo.service.UserDetailsImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class LoggingAspect {

    @Autowired
    private UserActivityService userActivityService;

    @Pointcut("within(com.example.demo.controller..*)")
    public void controllerMethods() {}

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String ipAddress = getClientIpAddress(); // 獲取 IP 地址

        String action = "執行了操作: " + methodName;
        ActionType actionType = ActionType.api_access;

        // 嘗試從方法名稱推斷 ActionType
        if (methodName.startsWith("create")) {
            actionType = determineActionType(className, "create");
            action = "創建了新項目";
        } else if (methodName.startsWith("update")) {
            actionType = determineActionType(className, "update");
            action = "更新了現有項目";
        } else if (methodName.startsWith("delete")) {
            actionType = determineActionType(className, "delete");
            action = "刪除了項目";
        } else if (methodName.startsWith("get")) {
            actionType = determineActionType(className, "view");
            action = "查看了項目";
        } else if (methodName.equals("registerUser")) {
            actionType = ActionType.register_success;
            action = "用戶註冊";
        } else if (methodName.equals("authenticateUser")) {
            actionType = ActionType.login_success;
            action = "用戶登入成功";
        } else if (methodName.equals("logoutUser")) {
            actionType = ActionType.logout;
            action = "用戶登出";
        }

        String details = "方法: " + className + "." + methodName + ", 參數: " + Arrays.toString(joinPoint.getArgs());
        if (result != null && result.getClass().getName().contains("Response")) {
             try {
                 Object idObject = result.getClass().getMethod("getId").invoke(result);
                 if (idObject instanceof Long) {
                     details += ", 影響實體ID: " + idObject;
                 }
             } catch (Exception e) { /* ignore if getId() or casting fails */ }
        }

        userActivityService.recordActivity(actionType, action, details, ipAddress);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String ipAddress = getClientIpAddress();

        String action = "操作失敗: " + methodName;
        ActionType actionType = ActionType.error;

        if (methodName.equals("authenticateUser")) {
            actionType = ActionType.login_failure;
            action = "用戶登入失敗";
        } else if (methodName.startsWith("create")) {
            actionType = determineActionType(className, "create_error");
        } else if (methodName.startsWith("update")) {
            actionType = determineActionType(className, "update_error");
        } else if (methodName.startsWith("delete")) {
            actionType = determineActionType(className, "delete_error");
        } else if (methodName.startsWith("get")) {
            actionType = determineActionType(className, "view_error");
        } else {
            actionType = ActionType.error;
        }

        String details = "方法: " + className + "." + methodName + ", 異常: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        if (joinPoint.getArgs() != null) {
            details += ", 參數: " + Arrays.toString(joinPoint.getArgs());
        }

        userActivityService.recordActivity(actionType, action, details, ipAddress);
    }

    // 輔助方法：根據控制器類名和操作類型推斷更具體的 ActionType
    private ActionType determineActionType(String className, String operation) {
        if (className.contains("ProjectController")) {
            if ("create".equals(operation)) return ActionType.project_create;
            if ("update".equals(operation)) return ActionType.project_update;
            if ("delete".equals(operation)) return ActionType.project_delete;
            if ("view".equals(operation)) return ActionType.api_access; // 視為一般 API 訪問
            if ("create_error".equals(operation)) return ActionType.error;
            if ("update_error".equals(operation)) return ActionType.error;
            if ("delete_error".equals(operation)) return ActionType.error;
            if ("view_error".equals(operation)) return ActionType.error;
            return ActionType.api_access; // 預設為 api_access
        } else if (className.contains("TodoItemController")) {
            if ("create".equals(operation)) return ActionType.todo_create; // ⭐ 修正 ⭐
            if ("update".equals(operation)) return ActionType.todo_update; // ⭐ 修正 ⭐
            if ("delete".equals(operation)) return ActionType.todo_delete; // ⭐ 修正 ⭐
            if ("view".equals(operation)) return ActionType.todo_view; // ⭐ 修正 ⭐
            if ("create_error".equals(operation)) return ActionType.error;
            if ("update_error".equals(operation)) return ActionType.error;
            if ("delete_error".equals(operation)) return ActionType.error;
            if ("view_error".equals(operation)) return ActionType.error;
            return ActionType.api_access; // 預設為 api_access
        } else if (className.contains("UserController")) {
            if ("create".equals(operation) || "register".equals(operation)) return ActionType.register_success;
            if ("update".equals(operation)) return ActionType.user_role_update; // ⭐ 修正 ⭐
            if ("delete".equals(operation)) return ActionType.admin_action; // 假設刪除用戶是管理員操作
            if ("view".equals(operation)) return ActionType.user_view_all; // ⭐ 修正 ⭐
            if ("create_error".equals(operation) || "register_error".equals(operation)) return ActionType.error;
            if ("update_error".equals(operation)) return ActionType.error;
            if ("delete_error".equals(operation)) return ActionType.error;
            if ("view_error".equals(operation)) return ActionType.error;
            return ActionType.user_view_all; // 預設為查看用戶
        } else if (className.contains("AuthController")) {
            if ("authenticate".equals(operation)) return ActionType.login_success;
            if ("register".equals(operation)) return ActionType.register_success;
            if ("logout".equals(operation)) return ActionType.logout;
            if ("authenticate_error".equals(operation)) return ActionType.login_failure;
            if ("register_error".equals(operation)) return ActionType.error;
            if ("logout_error".equals(operation)) return ActionType.error;
            return ActionType.api_access;
        }
        return ActionType.api_access;
    }

    // 從 HttpServletRequest 獲取客戶端 IP 地址
    private String getClientIpAddress() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return "unknown";
        }
        HttpServletRequest request = sra.getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) { // 此處有重複的 null 檢查
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress == null || "unknown".equalsIgnoreCase(ipAddress)) { // 此處有重複的 null 檢查
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    // 從 Spring Security 上下文獲取當前用戶名
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                return ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
            }
            return authentication.getName();
        }
        return "anonymous";
    }
}