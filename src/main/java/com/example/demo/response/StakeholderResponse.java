package com.example.demo.response;

public class StakeholderResponse {
    private Long id;
    private String name;
    private String email;
    private String interest;        // 新增欄位
    private String matrixStatus;   // 新增欄位
    private String phone;           // 新增欄位
    private String power;           // 新增欄位
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

    public String getInterest() { return interest; }
    public void setInterest(String interest) { this.interest = interest; }
    public String getMatrixStatus() { return matrixStatus; }
    public void setMatrixStatus(String matrixStatus) { this.matrixStatus = matrixStatus; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPower() { return power; }
    public void setPower(String power) { this.power = power; }
    public String getRequirement() { return requirement; }
    public void setRequirement(String requirement) { this.requirement = requirement; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}