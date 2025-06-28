package com.example.demo.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data // Lombok's @Data annotation automatically generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok's @NoArgsConstructor generates a constructor with no arguments
@AllArgsConstructor // Lombok's @AllArgsConstructor generates a constructor with all arguments
public class ProjectRequest {

    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 100, message = "Project name cannot exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Status cannot be empty")
    @Size(max = 50, message = "Status cannot exceed 50 characters")
    private String status; // e.g., "Active", "Completed", "On Hold"

    // Optional: List of stakeholder IDs to associate with the project
    private Set<Long> stakeholderIds;

    // ⭐ 修正點：Lombok 的 @Data 註解會自動生成 getStatus() 和 setStatus() 方法。
    // 如果您沒有使用 Lombok，或者 Lombok 配置有問題，您需要手動添加它們。
    // 為了確保萬無一失，這裡手動列出 getter 和 setter (雖然 @Data 已經包含了)
    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }
}