package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationDDto {
	
	private String projectName;
	private Integer projectId;
	private String classification;
	private Integer toDoListId;
	private String toDoList;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private String userName;
	private Boolean confidential;
	
		
}
