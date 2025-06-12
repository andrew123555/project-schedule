package com.example.demo.service;

import java.util.List;


import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDao;



public interface ProjectService {

	List<ProjectInformationDao> findAllProjects() ;
	ProjectInformationDao getProjectById(Integer ProjectId) throws ProjectException;
	void addProject(ProjectInformationDao projectInformationDao) throws ProjectException;
	void updateProject(Integer projectId, ProjectInformationDao projectInformationDao) throws ProjectException;
	void updateProjectName(Integer projectId, String projectName) throws ProjectException;
	void deleteProject(Integer ProjectId) throws ProjectException;
}
