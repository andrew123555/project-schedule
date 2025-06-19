package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationBDto {
	
	private Integer projectId;
	private String projectName;
	private String userName;
	private String department;
	private Boolean projectStatu;
}
