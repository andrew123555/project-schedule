package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.model.dto.ProjectInformationDDto;
import com.example.demo.model.entity.ProjectInformationD;

@Component 
public class ProjectMapper {
	
	@Autowired
	private ModelMapper modelMapper2;
	
	public ProjectInformationDDto ttoDto(ProjectInformationD projectInformationD) {
		return modelMapper2.map(projectInformationD, ProjectInformationDDto.class);
	}
	
	public ProjectInformationD ttoEntity(ProjectInformationDDto projectInformationDDto) {
		return modelMapper2.map(projectInformationDDto, ProjectInformationD.class);
	}
	
}