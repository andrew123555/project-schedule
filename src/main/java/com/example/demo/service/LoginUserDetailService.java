package com.example.demo.service;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.model.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;


/***
 * 该类需要实现接口UserDetailsService，
 * 主要是实现loadUserByUsername方法：
 * 用来加载用户保存在数据库中的登录信息
 */
@Component
@Slf4j
public class LoginUserDetailService implements UserDetailsService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("用户的登录信息被加载了");

        //通过username获取用户信息
        UserInfo userInfo = iUserService.getUserByName(username);
        log.info("数据库中保存的用户信息" + userInfo);
        if(userInfo == null) {
            throw new UsernameNotFoundException("not found");
        }

        //定义权限列表.
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 用户可以访问的资源名称（或者说用户所拥有的权限） 注意：必须"ROLE_"开头
        authorities.add(new SimpleGrantedAuthority("ROLE_"+userInfo.getRole()));

        //注意这里的user为security内置的用户类型，类型为org.springframework.security.core.userdetails.User
        User userDetails = new User(userInfo.getUsername(),passwordEncoder.encode(userInfo.getPassword()),authorities);
        log.info(userDetails.toString());
        return userDetails;
    }

}
