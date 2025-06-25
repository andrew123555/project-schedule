package com.example.demo.payload;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    private String name;
    private String description;
    private String status;
    // 與 Project Entity 中的欄位匹配
}