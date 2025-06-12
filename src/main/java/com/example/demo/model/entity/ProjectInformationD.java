package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationD {
	
	private String classification;
	private String userName;
	private String toDoList;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private Boolean confidential;
		

}
