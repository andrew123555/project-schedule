package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.dto.ProjectInformationDDto;


public interface ProjectIMRepository {

	List<ProjectInformationDDto> findAllProjectIMs();
	Optional<ProjectInformationDDto> getProjectIMById(Integer toDoListId);
	boolean addProjectIM(ProjectInformationDDto projectInformationDDto);
	boolean updateProjectIM(Integer toDoListId, ProjectInformationDDto projectInformationDDto);
	boolean deleteProjectIM(Integer toDoListId);
}
