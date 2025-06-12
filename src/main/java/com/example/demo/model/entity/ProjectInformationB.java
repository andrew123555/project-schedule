package com.example.demo.model.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInformation {

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
