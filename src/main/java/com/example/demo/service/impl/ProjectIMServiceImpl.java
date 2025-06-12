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
		return projectIMRepository.findAllProjectIMs();
	}

	@Override
	public ProjectInformationDDto getProjectIMById(Integer toDoListId) throws ProjectException {
		Optional<ProjectInformationDDto> optProjectIM = projectIMRepository.getProjectIMById(toDoListId);
		if(optProjectIM.isEmpty()) {
			throw new ProjectException("id: " + toDoListId + ", 查無此待辦事項");
		}
		return optProjectIM.get();
	}

	@Override
	public void addProjectIM(ProjectInformationDDto projectInformationDDto) throws ProjectException {
		if(!projectIMRepository.addProjectIM(projectInformationDDto)) {
			throw new ProjectException("新增失敗, " + projectInformationDDto);
		}
		
	}

	@Override
	public void updateProjectIM(Integer toDoListId, ProjectInformationDDto projectInformationDDto) throws ProjectException {
		if(!projectIMRepository.updateProjectIM(toDoListId, projectInformationDDto)) {
			throw new ProjectException("修改失敗, id: " + toDoListId + ", " + projectInformationDDto);
		}
	}

	@Override
	public void updateProjectName(Integer toDoListId, String toDoList) throws ProjectException {
		ProjectInformationDDto projectInformationDDto = getProjectIMById(toDoListId);
		projectInformationDDto.setToDoList(toDoList);
		updateProjectIM(projectInformationDDto.getToDoListId(), projectInformationDDto);
	}

	@Override
	public void deleteProjectIM(Integer toDoListId) throws ProjectException {
		if(!projectIMRepository.deleteProjectIM(toDoListId)) {
			throw new ProjectException("刪除失敗, Id: " + toDoListId);
		}
		
	}

}
