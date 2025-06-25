package com.example.demo.aop;

import com.example.demo.model.entity.UserActivity;
import com.example.demo.model.entity.UserActivity.ActionType;
import com.example.demo.repository.UserActivityRepository;
import com.example.demo.security.jwt.JwtUtils; // 確保這個 import 存在，如果沒有用到可以移除

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

@Aspect
@Component
public class LogUserActivityAspect {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired(required = false) // 設為 false，表示不是必須的，防止因無法注入而報錯
    private JwtUtils jwtUtils;

    // --- 定義切入點 (Pointcuts) ---
    @Pointcut("execution(* com.example.demo.controller.AuthController.authenticateUser(..))")
    public void authLoginPointcut() {}

    @Pointcut("execution(* com.example.demo.controller.AuthController.registerUser(..))")
    public void authRegisterPointcut() {}

    @Pointcut("execution(* com.example.demo.controller.ProjectController.*(..))")
    public void projectControllerPointcut() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && execution(public * *(..))")
    public void allRestControllerMethods() {}

    // --- 輔助方法 ---

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "N/A (未登入)";
    }

    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "N/A (非HTTP請求)";
        }
        HttpServletRequest request = attributes.getRequest();
        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }

    // ⭐ 修改 saveActivity 方法：添加 'action' 參數
    private void saveActivity(String username, ActionType actionType, String details, String action) {
        String ipAddress = getClientIpAddress();
        // ⭐ 這裡傳入正確的 'action' 參數
        UserActivity activity = new UserActivity(username, action, actionType, details, ipAddress);
        userActivityRepository.save(activity);
    }

    // --- 實作日誌邏輯 ---

    @AfterReturning(pointcut = "authLoginPointcut()", returning = "result")
    public void logLoginSuccess(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String username = getCurrentUsername();
                if ("N/A (未登入)".equals(username)) {
                    if (responseEntity.getBody() instanceof Map) {
                        Map<String, Object> body = (Map<String, Object>) responseEntity.getBody();
                        if (body.containsKey("username")) {
                            username = body.get("username").toString();
                        }
                    }
                }
                // ⭐ 調用 saveActivity 時，傳入 'action' 值
                saveActivity(username, ActionType.LOGIN_SUCCESS, "使用者成功登入。", "登入成功");
            }
        }
    }

    @AfterThrowing(pointcut = "authLoginPointcut()", throwing = "ex")
    public void logLoginFailure(JoinPoint joinPoint, Throwable ex) {
        String username = "N/A (登入失敗)";
        String details = "登入失敗。錯誤訊息: " + ex.getMessage();
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Map) {
            Map<String, Object> loginRequest = (Map<String, Object>) args[0];
            if (loginRequest.containsKey("username")) {
                username = loginRequest.get("username").toString();
            }
        }
        // ⭐ 調用 saveActivity 時，傳入 'action' 值
        saveActivity(username, ActionType.LOGIN_FAILED, details, "登入失敗");
    }

    @Before("execution(* com.example.demo.controller.AuthController.logoutUser(..))")
    public void logLogout(JoinPoint joinPoint) {
        String username = getCurrentUsername();
        // ⭐ 調用 saveActivity 時，傳入 'action' 值
        saveActivity(username, ActionType.LOGOUT, "使用者成功登出。", "登出");
    }

    @AfterReturning(pointcut = "authRegisterPointcut()", returning = "result")
    public void logRegister(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String username = "新用戶註冊";
                Object[] args = joinPoint.getArgs();
                if (args.length > 0 && args[0] instanceof Map) {
                    Map<String, Object> signupRequest = (Map<String, Object>) args[0];
                    if (signupRequest.containsKey("username")) {
                        username = signupRequest.get("username").toString();
                    }
                }
                // ⭐ 調用 saveActivity 時，傳入 'action' 值
                saveActivity(username, ActionType.REGISTER, "新用戶成功註冊。", "註冊");
            }
        }
    }

    @AfterReturning(pointcut = "projectControllerPointcut()", returning = "result")
    public void logProjectActions(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String username = getCurrentUsername();
        String details = "";
        ActionType actionType = ActionType.API_ACCESS;
        String actionDescription = "API訪問"; // ⭐ 定義一個 action 變數來存儲具體的操作描述

        switch (methodName) {
            case "createProject":
                actionType = ActionType.CREATE_PROJECT;
                actionDescription = "新增項目";
                if (result instanceof ResponseEntity && ((ResponseEntity<?>) result).getBody() instanceof Map) {
                    Map<String, Object> body = (Map<String, Object>)((ResponseEntity<?>) result).getBody();
                    details = "新增項目: " + body.getOrDefault("data", "N/A").toString();
                } else {
                    details = "新增項目。";
                }
                break;
            case "updateProject":
                actionType = ActionType.UPDATE_PROJECT;
                actionDescription = "更新項目";
                details = "更新項目。";
                if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof Long) {
                    details = "更新項目 ID: " + joinPoint.getArgs()[0].toString();
                }
                break;
            case "deleteProject":
                actionType = ActionType.DELETE_PROJECT;
                actionDescription = "刪除項目";
                details = "刪除項目。";
                if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof Long) {
                    details = "刪除項目 ID: " + joinPoint.getArgs()[0].toString();
                }
                break;
            case "getAllProjects":
                actionType = ActionType.VIEW_ALL_PROJECTS;
                actionDescription = "查看所有項目";
                details = "查看所有項目列表。";
                break;
            case "createProjectItem":
                actionType = ActionType.CREATE_PROJECT_ITEM;
                actionDescription = "新增項目細節";
                if (result instanceof ResponseEntity && ((ResponseEntity<?>) result).getBody() instanceof Map) {
                    Map<String, Object> body = (Map<String, Object>)((ResponseEntity<?>) result).getBody();
                    details = "新增項目細節: " + body.getOrDefault("data", "N/A").toString();
                } else {
                    details = "新增項目細節。";
                }
                break;
            case "updateProjectItem":
                actionType = ActionType.UPDATE_PROJECT_ITEM;
                actionDescription = "更新項目細節";
                details = "更新項目細節。";
                if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof String) {
                    details = "更新項目細節 ID: " + joinPoint.getArgs()[0].toString();
                }
                break;
            case "deleteProjectItem":
                actionType = ActionType.DELETE_PROJECT_ITEM;
                actionDescription = "刪除項目細節";
                details = "刪除項目細節。";
                if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof String) {
                    details = "刪除項目細節 ID: " + joinPoint.getArgs()[0].toString();
                }
                break;
            case "getAllProjectItems":
                actionType = ActionType.API_ACCESS;
                actionDescription = "查看所有項目細節";
                details = "查看所有項目細節列表。";
                break;
            case "getProjectItemsByProjectId":
                actionType = ActionType.VIEW_PROJECT_ITEMS_BY_ID;
                actionDescription = "查看特定項目細節";
                details = "查看特定項目 ID 的細節。";
                if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] instanceof Long) {
                    details = "查看項目 ID: " + joinPoint.getArgs()[0].toString() + " 的細節。";
                }
                break;
            default:
                actionDescription = "執行方法";
                details = "執行方法: " + methodName;
                break;
        }
        // ⭐ 調用 saveActivity 時，傳入定義好的 actionDescription 變數
        saveActivity(username, actionType, details, actionDescription);
    }
}