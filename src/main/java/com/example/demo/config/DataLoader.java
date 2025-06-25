package com.example.demo.config;

import com.example.demo.model.entity.ERole;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. 初始化角色
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            System.out.println("Added ROLE_USER to database.");
        }
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            System.out.println("Added ROLE_ADMIN to database.");
        }

        // 2. 初始化一個管理員帳號
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "admin@example.com", encoder.encode("admin123")); // 設定一個初始密碼
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add); // 管理員也擁有 USER 角色
            admin.setRoles(roles);
            userRepository.save(admin);
            System.out.println("Created default admin user: admin/admin123");
        }

        // 3. 初始化一個普通用戶 (fu062j0, m/4)
        if (userRepository.findByUsername("fu062j0").isEmpty()) {
            User user = new User("fu062j0", "fu062j0@example.com", encoder.encode("m/4"));
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            user.setRoles(roles);
            userRepository.save(user);
            System.out.println("Created default user: fu062j0/m/4");
        }
    }
}