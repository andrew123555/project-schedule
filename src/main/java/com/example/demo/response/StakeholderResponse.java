package com.example.demo.response;

import com.example.demo.model.entity.Stakeholder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StakeholderResponse {
    private Long id;
    private String name;
    private String email;
    private String role; 
    private String contactInfo;
    private String phone; // ⭐ 新增 phone 欄位 ⭐
    private String requirement; // ⭐ 新增 requirement 欄位 ⭐
    private String power; // ⭐ 新增 power 欄位 ⭐
    private String interest; // ⭐ 新增 interest 欄位 ⭐
    private String matrixStatus; // ⭐ 新增 matrixStatus 欄位 ⭐

    private Long userId; // For associated User
    private String username; // For associated User

    public StakeholderResponse(Stakeholder stakeholder) {
        this.id = stakeholder.getId();
        this.name = stakeholder.getName();
        this.email = stakeholder.getEmail();
        this.role = stakeholder.getRole();
        this.contactInfo = stakeholder.getContactInfo();
        this.phone = stakeholder.getPhone(); // ⭐ 設置 phone ⭐
        this.requirement = stakeholder.getRequirement(); // ⭐ 設置 requirement ⭐
        this.power = stakeholder.getPower(); // ⭐ 設置 power ⭐
        this.interest = stakeholder.getInterest(); // ⭐ 設置 interest ⭐
        this.matrixStatus = stakeholder.getMatrixStatus(); // ⭐ 設置 matrixStatus ⭐

        if (stakeholder.getUser() != null) {
            this.userId = stakeholder.getUser().getId();
            this.username = stakeholder.getUser().getUsername();
        }
    }
}