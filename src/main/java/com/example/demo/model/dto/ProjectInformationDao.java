package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformationDao {
	
	private Integer projectId;
	private String projectName;
	private String userName;
	//private String function;
	private String department;
	//private String email;
	//private String Schedule_startTime;
	//private String Schedule_endTime;
	private Boolean projectStatu;
	//private String confidential;
	//private String Shakeholder;
	//private String requirement;
		
}
