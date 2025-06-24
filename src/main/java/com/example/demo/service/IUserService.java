package com.example.demo.service;

import com.example.demo.model.entity.UserInfo;

public interface IUserService {

    UserInfo getUserByName(String username);
}
