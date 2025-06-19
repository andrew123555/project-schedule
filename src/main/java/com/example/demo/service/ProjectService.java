package com.example.demo.service;

import java.util.List;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationBDto;

public interface ProjectService {

	List<ProjectInformationBDto> findAllProjects() ;
	ProjectInformationBDto getProjectById(Integer ProjectId) throws ProjectException;
	ProjectInformationBDto getProjectByName(Integer projectId) throws ProjectException;

	void addProject(ProjectInformationBDto projectInformationDao) throws ProjectException;
	void updateProject(Integer projectId, ProjectInformationBDto projectInformationDao) throws ProjectException;
	void deleteProject(Integer projectId) throws ProjectException;
}
