package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ProjectException;
import com.example.demo.exception.ProjectNotFoundException;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.model.dto.ProjectInformationDDto;
import com.example.demo.model.entity.ProjectInformationD;
import com.example.demo.repository.ProjectIMRepository;
import com.example.demo.service.ProjectIMService;

@Service
public class ProjectIMServiceImpl implements ProjectIMService{

	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	@Qualifier("projectIMRepositoryJdbcImpl") 
	private ProjectIMRepository projectIMRepository;
	
	
	@Override
	public List<ProjectInformationDDto> findAllProjectIMs() {
		return projectIMRepository.findAllProjectIMs();
	}

	@Override
	public ProjectInformationDDto getProjectIMById(Integer projectId) throws ProjectException {
		Optional<ProjectInformationDDto> optProjectIM = projectIMRepository.getProjectIMById(projectId);
		if(optProjectIM.isEmpty()) {
			throw new ProjectException("projectId: " + projectId + ", 查無此待辦事項");
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
			throw new ProjectException("修改失敗, toDoListId: " + toDoListId + ", " + projectInformationDDto);
		}
	}

	

	@Override
	public void deleteProjectIM(Integer toDoListId) throws ProjectException {
		if(!projectIMRepository.deleteProjectIM(toDoListId)) {
			throw new ProjectException("刪除失敗, Id: " + toDoListId);
		}
		
	}

}
