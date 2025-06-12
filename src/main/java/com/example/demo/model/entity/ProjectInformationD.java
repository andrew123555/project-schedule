package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationD {
	
	private Boolean classification;
	private String userName;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private String confidential;
		

}
