package com.example.demo.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserRoleUpdateRequest {
    
	@NotEmpty(message = "角色列表不能為空") // 修正：使用 @NotEmpty 驗證集合不為空
    @Size(min = 1, message = "至少需要一個角色")
    private Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}