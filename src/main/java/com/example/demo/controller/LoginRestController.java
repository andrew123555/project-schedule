package com.example.demo.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CertService;


import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/rest")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"}, allowCredentials = "true")
public class LoginRestController {
	Logger logger = LoggerFactory.getLogger(getClass());
	//private static Logger logger = LoggerFactory.getLogger(LoginRestController.class);//导入日志
	@Autowired
	private CertService certService;
	
	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
	    try {
	        UserCert cert = certService.getCert(username, password);
	        session.setAttribute("userCert", cert);
	        logger.info("用户 {} 成功登入 ",  username);
	        return ResponseEntity.ok(ApiResponse.success("登入成功", null));
	        		
	        		
	    } catch (CertException e) {
	    	logger.error("用户 {} 登入失敗 ",  username);
	    	return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(ApiResponse.error(401, "登入失敗: " + e.getMessage()));
	    }
	}
	
	@GetMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
	    if(session.getAttribute("userCert") == null) {
	    	return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .body(ApiResponse.error(401, "登出失敗: 尚未登入 "));
	    }
	    session.invalidate();
	    return ResponseEntity.ok(ApiResponse.success("登出成功", null));
	}
	
	@GetMapping("/check-login")
	public ResponseEntity<ApiResponse<Boolean>> checkLogin(HttpSession session) {
	    boolean loggedIn = session.getAttribute("userCert") != null;
	    return ResponseEntity.ok(ApiResponse.success("檢查登入", loggedIn));
	}
	
}