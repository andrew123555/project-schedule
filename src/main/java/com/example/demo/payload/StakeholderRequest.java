package com.example.demo.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Lombok's @Data annotation automatically generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok's @NoArgsConstructor generates a constructor with no arguments
@AllArgsConstructor // Lombok's @AllArgsConstructor generates a constructor with all arguments
public class StakeholderRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 50, message = "Phone cannot exceed 50 characters")
    private String phone;

    @Size(max = 1000, message = "Requirement cannot exceed 1000 characters")
    private String requirement;

    @NotNull(message = "Power cannot be null")
    private String power; 

    @NotNull(message = "Interest cannot be null")
    private String interest; 

    @Size(max = 50, message = "Matrix status cannot exceed 50 characters")
    private String matrixStatus;

    @NotBlank(message = "Role cannot be empty") 
    @Size(max = 50, message = "Role cannot exceed 50 characters")
    private String role; 

    // ⭐ 關鍵修正點：明確添加 getter 方法（如果 Lombok 未能正常工作）⭐
    // 如果您確定 Lombok 配置正確，這些方法可以省略，因為 @Data 會自動生成。
    // 但為了排除 Lombok 配置問題，暫時將它們保留。
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRequirement() {
        return requirement;
    }

    public String getPower() {
        return power;
    }

    public String getInterest() {
        return interest;
    }

    public String getMatrixStatus() {
        return matrixStatus;
    }

    public String getRole() {
        return role;
    }

    // 同樣，setter 方法也會由 @Data 生成，但如果您不使用 Lombok，也需要手動添加：
    // public void setName(String name) { this.name = name; }
    // public void setEmail(String email) { this.email = email; }
    // public void setPhone(String phone) { this.phone = phone; }
    // public void setRequirement(String requirement) { this.requirement = requirement; }
    // public void setPower(Integer power) { this.power = power; }
    // public void setInterest(Integer interest) { this.interest = interest; }
    // public void setMatrixStatus(String matrixStatus) { this.matrixStatus = matrixStatus; }
    // public void setRole(String role) { this.role = role; }
}