package com.example.demo.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class StakeholderRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Size(max = 255)
    @Email(message = "Invalid email format")
    private String email;

    private String interest;
    private String matrixStatus;
    private String phone;
    private String power;
    private String requirement;

    private Long projectId; // 如果需要關聯專案

    // Getters and Setters for all fields
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
}