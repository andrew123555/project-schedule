package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.model.entity.UserInfo;

@Mapper
public interface UserMapper {

    //通过用户名查询用户
    UserInfo getUserByName(@Param("username") String username);

}
