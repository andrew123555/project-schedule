package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.dto.ProjectInformationDDto;


public interface ProjectIMRepository {

	List<ProjectInformationDDto> findAllProjectIMs();
	Optional<ProjectInformationDDto> getProjectByName(String projectName);
	boolean addProjectIM(ProjectInformationDDto projectInformationDDto);
	boolean updateProjectIM(String projectName, ProjectInformationDDto projectInformationDDto);
	boolean deleteProjectIM(String projectName);
}
