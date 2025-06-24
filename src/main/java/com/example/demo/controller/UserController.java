package com.example.demo.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.entity.UserInfo;
import com.example.demo.service.IUserService;

@RestController
@RequestMapping("/api/user")
@Api(description = "权限测试", tags = "权限测试")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @GetMapping("/getUser")
    @ApiOperation(value = "用户权限测试接口", notes = "用户权限测试接口")
    public UserInfo getUser(@RequestParam String username) {
        return iUserService.getUserByName(username);
    }
}

