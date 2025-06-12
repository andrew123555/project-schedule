package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.dto.ProjectInformationDao;


public interface ProjectRepository {

	List<ProjectInformationDao> findAllProjects();
	Optional<ProjectInformationDao> getProjectById(Integer projectId);
	boolean addProject(ProjectInformationDao projectInformationDao);
	boolean updateProject(Integer projectId, ProjectInformationDao projectInformationDao);
	boolean deleteProject(Integer projectId);
}
