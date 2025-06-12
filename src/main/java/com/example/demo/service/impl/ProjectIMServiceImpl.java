package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDDto;
import com.example.demo.repository.ProjectIMRepository;
import com.example.demo.service.ProjectIMService;

@Service
public class ProjectIMServiceImpl implements ProjectIMService{

	@Autowired
	@Qualifier("projectIMRepositoryJdbcImpl") 
	private ProjectIMRepository projectIMRepository;
	
	@Override
	public List<ProjectInformationDDto> findAllProjectIMs() {
		return projectRepository.findAllProjectIMs();
	}

	@Override
	public ProjectInformationDDto getProjectByName(String projectName) throws ProjectException {
		Optional<ProjectInformationBDto> optProject = projectRepository.getProjectById(projectId);
		if(optProject.isEmpty()) {
			throw new ProjectException("id: " + projectId + ", 查無此項目");
		}
		return optProject.get();
	}

	@Override
	public void addProject(ProjectInformationDDto projectInformationDDto) throws ProjectException {
		if(!projectRepository.addProject(projectInformationDao)) {
			throw new ProjectException("新增失敗, " + projectInformationDao);
		}
		
	}

	@Override
	public void updateProject(String projectName, ProjectInformationDDto projectInformationDDto) throws ProjectException {
		if(!projectRepository.updateProject(projectId, projectInformationDao)) {
			throw new ProjectException("修改失敗, id: " + projectId + ", " + projectInformationDao);
		}
	}

	@Override
	public void updateProjectName(String projectName, String projectName) throws ProjectException {
		ProjectInformationDDto projectInformationDDto = getProjectByName(projectName);
		projectInformationDao.setProjectName(projectName);
		updateProject(projectInformationDDto.getClassification(), projectInformationDDto);
	}

	@Override
	public void deleteProjectIM(String projectName) throws ProjectException {
		if(!projectIMRepository.deleteProjectIM(projectName)) {
			throw new ProjectException("刪除失敗, name: " + projectName);
		}
		
	}

}
