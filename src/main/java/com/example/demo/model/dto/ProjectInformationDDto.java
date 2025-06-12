package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationDDto {
	
	private Integer toDoListId;
	private String userName;
	private String toDoList;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private Boolean confidential;
	private String classification;
		
}
