package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationBDto;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	@Qualifier("projectRepositoryJdbcImpl") 
	private ProjectRepository projectRepository;
	
	@Override
	public List<ProjectInformationBDto> findAllProjects() {
		return projectRepository.findAllProjects();
	}

	@Override
	public ProjectInformationBDto getProjectById(Integer projectId) throws ProjectException {
		Optional<ProjectInformationBDto> optProject = projectRepository.getProjectById(projectId);
		if(optProject.isEmpty()) {
			throw new ProjectException("id: " + projectId + ", 查無此項目");
		}
		return optProject.get();
	}

	@Override
	public void addProject(ProjectInformationBDto projectInformationDao) throws ProjectException {
		if(!projectRepository.addProject(projectInformationDao)) {
			throw new ProjectException("新增失敗, " + projectInformationDao);
		}
		
	}

	@Override
	public void updateProject(Integer projectId, ProjectInformationBDto projectInformationDao) throws ProjectException {
		if(!projectRepository.updateProject(projectId, projectInformationDao)) {
			throw new ProjectException("修改失敗, id: " + projectId + ", " + projectInformationDao);
		}
	}

	@Override
	public void updateProjectName(Integer projectId, String projectName) throws ProjectException {
		ProjectInformationBDto projectInformationDao = getProjectById(projectId);
		projectInformationDao.setProjectName(projectName);
		updateProject(projectInformationDao.getProjectId(), projectInformationDao);
	}

	@Override
	public void deleteProject(Integer projectId) throws ProjectException {
		if(!projectRepository.deleteProject(projectId)) {
			throw new ProjectException("刪除失敗, id: " + projectId);
		}
		
	}

}
