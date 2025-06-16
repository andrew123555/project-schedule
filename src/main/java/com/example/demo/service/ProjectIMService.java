package com.example.demo.service;

import java.util.List;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDDto;


public interface ProjectIMService {

	List<ProjectInformationDDto> findAllProjectIMs() ;
	ProjectInformationDDto getProjectIMById(Integer projectId) throws ProjectException;
	void addProjectIM(ProjectInformationDDto projectInformationDDto) throws ProjectException;
	void updateProjectIM(Integer toDoListId, ProjectInformationDDto projectInformationDDto) throws ProjectException;
	void deleteProjectIM(Integer toDoListId) throws ProjectException;
}
