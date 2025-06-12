package com.example.demo.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.model.dto.ProjectInformationDDto;
import com.example.demo.repository.ProjectIMRepository;


@Repository
public class ProjectIMRepositoryJdbcImpl implements ProjectIMRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@Override
	public List<ProjectInformationDDto> findAllProjectIMs() {
		String sql = "select projectName, classification,  toDoList, scheduleStartTime, scheduleEndTime, userName, confidential from projectinformationD";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ProjectInformationDDto.class));
	}

	@Override
	public Optional<ProjectInformationDDto> getProjectByName(String projectName) {
		String sql = "select projectName, classification,  toDoList, scheduleStartTime, scheduleEndTime, userName, confidential from projectinfromationD where projectName=?";
		try {
			ProjectInformationDDto projectInformationDDao = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ProjectInformationDDto.class), projectName);
			return Optional.of(projectInformationDDao);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
		
	}

	@Override
	public boolean addProjectIM(ProjectInformationDDto projectInformationDDto) {
		String sql = "insert into projectinformationD(classification,  toDoList, scheduleStartTime, scheduleEndTime, userName, confidential) values(?, ?, ?, ?, ?, ?)";
		int rows = jdbcTemplate.update(sql, projectInformationDDto.getClassification(), projectInformationDDto.getToDoList(), projectInformationDDto.getScheduleStartTime(), projectInformationDDto.getScheduleEndTime(), projectInformationDDto.getUserName(), projectInformationDDto.getConfidential() );
		return rows > 0;
	}

	@Override
	public boolean updateProjectIM(String projectName, ProjectInformationDDto projectInformationDDto) {
		String sql = "update projectinformationD set classification = ?, toDoList = ?, scheduleStartTime = ?, scheduleEndTime = ? , userName = ? , confidential = ? where projectName = ?";
		int rows = jdbcTemplate.update(sql, projectInformationDDto.getClassification(), projectInformationDDto.getToDoList(), projectInformationDDto.getScheduleStartTime(), projectInformationDDto.getScheduleEndTime(),projectInformationDDto.getUserName(),projectInformationDDto.getConfidential(), projectName);
		return rows > 0;
	}

	@Override
	public boolean deleteProjectIM(String projectName) {
		String sql = "delete from projectinformationD where projectName = ?";
		int rows = jdbcTemplate.update(sql, projectName);
		return rows > 0;
	}
}
