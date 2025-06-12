package com.example.demo.service;

import java.util.List;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDDto;

public interface ProjectIMService {

	List<ProjectInformationDDto> findAllProjectIMs() ;
	ProjectInformationDDto getProjectByName(String ProjectName) throws ProjectException;
	void addProjectIM(ProjectInformationDDto projectInformationDDto) throws ProjectException;
	void updateProjectIM(String ProjectName, ProjectInformationDDto projectInformationDDto) throws ProjectException;
	void updateProjectName(String ProjectName, String projectName) throws ProjectException;
	void deleteProjectIM(String ProjectName) throws ProjectException;
}
