package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationDDto {
	
	
	private String userName;
	private String scheduleStartTime;
	private String scheduleEndTime;
	private String confidential;
	private Boolean classification;
		
}
