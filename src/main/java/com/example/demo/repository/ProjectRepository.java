package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.dto.ProjectInformationBDto;


public interface ProjectRepository {

	List<ProjectInformationBDto> findAllProjects();
	Optional<ProjectInformationBDto> getProjectById(Integer projectId);
	boolean addProject(ProjectInformationBDto projectInformationDao);
	boolean updateProject(Integer projectId, ProjectInformationBDto projectInformationDao);
	boolean deleteProject(Integer projectId);
}
