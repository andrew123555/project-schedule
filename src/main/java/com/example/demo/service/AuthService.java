package com.example.demo.service;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.UserActivity;
import com.example.demo.model.entity.UserActivity.ActionType;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserActivityRepository; // 假設您有這個 Repository
import com.example.demo.payload.LoginRequest;
import com.example.demo.payload.SignupRequest;
import com.example.demo.response.MessageResponse;
import com.example.demo.response.UserInfoResponse;
import com.example.demo.security.jwt.JwtUtils;
import com.example.demo.service.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest; // 導入 HttpServletRequest
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserActivityRepository userActivityRepository; // ⭐ 注入 UserActivityRepository

	// 登入方法
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest, HttpServletRequest request) { // ⭐ 添加
																										// HttpServletRequest
																										// 參數
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		// ⭐ 記錄登入活動
		String ipAddress = getClientIpAddress(request); // 獲取客戶端 IP
		recordUserActivity(userDetails.getUsername(), ActionType.LOGIN_SUCCESS, "用戶成功登入。", ipAddress, "登入"); // ⭐
																												// 'action'
																												// 字段值

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	// 註冊方法
	public ResponseEntity<?> registerUser(SignupRequest signUpRequest, HttpServletRequest request) { // ⭐ 添加
																										// HttpServletRequest
																										// 參數
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("錯誤: 此使用者名稱已被使用！"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("錯誤: 此電子郵件已被使用！"));
		}

		// 創建新用戶帳戶
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		// TODO: 處理角色分配 (假設您有預設角色或從請求中獲取角色)
		// user.setRoles(Collections.singleton(new Role(ERole.ROLE_USER))); // 範例

		userRepository.save(user);

		// ⭐ 記錄註冊活動
		String ipAddress = getClientIpAddress(request); // 獲取客戶端 IP
		recordUserActivity(user.getUsername(), ActionType.REGISTER, "新用戶註冊成功。", ipAddress, "註冊"); // ⭐ 'action' 字段值

		return ResponseEntity.ok(new MessageResponse("使用者註冊成功！"));
	}

	// 登出方法
	public ResponseEntity<?> logoutUser(HttpServletRequest request) { // ⭐ 添加 HttpServletRequest 參數
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

		// ⭐ 記錄登出活動 (需要獲取當前用戶名)
		String username = "Anonymous"; // 預設值，如果無法獲取
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
			username = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
		}
		String ipAddress = getClientIpAddress(request);
		recordUserActivity(username, ActionType.LOGOUT, "用戶成功登出。", ipAddress, "登出"); // ⭐ 'action' 字段值

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new MessageResponse("您已登出！"));
	}

	// ⭐ 輔助方法：記錄用戶活動
	private void recordUserActivity(String username, ActionType actionType, String details, String ipAddress,
			String action) {
		try {
			UserActivity userActivity = new UserActivity(username, action, actionType, details, ipAddress); // ⭐
																											// 使用新更新的五參數構造函數
			// timestamp 在構造函數中自動設置了

			userActivityRepository.save(userActivity);
		} catch (Exception e) {
			// 記錄錯誤，但不要阻止主要業務邏輯
			System.err.println("記錄用戶活動失敗: " + e.getMessage());
			e.printStackTrace(); // 打印堆棧追蹤以進行調試
		}
	}

	// ⭐ 輔助方法：獲取客戶端 IP 地址
	private String getClientIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}