package com.example.demo.response;

public class StakeholderResponse {
    private Long id;
    private String name;
    private String email;
    private boolean interest;        // 新增欄位
    private boolean power;  
    private String matrixStatus;   // 新增欄位
    private String phone;           // 新增欄位
    private String requirement;     // 新增欄位
    private Long projectId;
    private String projectName; // 顯示專案名稱

    // Getters and Setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    
    public boolean isInterest() { return interest; }
    public void setInterest(boolean interest) { this.interest = interest; }
    
    
    public String getMatrixStatus() { return matrixStatus; }
    public void setMatrixStatus(String matrixStatus) { this.matrixStatus = matrixStatus; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
   
    
    public boolean isPower() { return power; }
    public void setPower(boolean power) { this.power = power; }
   
    
    public String getRequirement() { return requirement; }
    public void setRequirement(String requirement) { this.requirement = requirement; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}