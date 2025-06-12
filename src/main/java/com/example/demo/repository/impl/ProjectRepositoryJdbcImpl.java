package com.example.demo.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.model.dto.ProjectInformationBDto;
import com.example.demo.repository.ProjectRepository;


@Repository
public class ProjectRepositoryJdbcImpl implements ProjectRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate; 
	
	@Override
	public List<ProjectInformationBDto> findAllProjects() {
		String sql = "select projectId, projectName, department, userName, projectStatu from projectinformation";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ProjectInformationBDto.class));
	}

	@Override
	public Optional<ProjectInformationBDto> getProjectById(Integer projectId) {
		String sql = "select projectId, projectName, department, userName, projectStatu from projectinfromation where projectId=?";
		try {
			ProjectInformationBDto projectInformationDao = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ProjectInformationBDto.class), projectId);
			return Optional.of(projectInformationDao);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
		
	}

	@Override
	public boolean addProject(ProjectInformationBDto projectInformationDao) {
		String sql = "insert into projectinformation(projectName, department, userName, projectStatu) values(?, ?, ?, ?)";
		int rows = jdbcTemplate.update(sql, projectInformationDao.getProjectName(), projectInformationDao.getDepartment(), projectInformationDao.getUserName(), projectInformationDao.getProjectStatu());
		return rows > 0;
	}

	@Override
	public boolean updateProject(Integer projectId, ProjectInformationBDto projectInformationDao) {
		String sql = "update projectinformation set projectName = ?, department = ?, userName = ?, projectStatu = ? where projectId = ?";
		int rows = jdbcTemplate.update(sql, projectInformationDao.getProjectName(), projectInformationDao.getDepartment(), projectInformationDao.getUserName(), projectInformationDao.getProjectStatu(), projectId);
		return rows > 0;
	}

	@Override
	public boolean deleteProject(Integer projectId) {
		String sql = "delete from projectinformation where projectId = ?";
		int rows = jdbcTemplate.update(sql, projectId);
		return rows > 0;
	}
}
