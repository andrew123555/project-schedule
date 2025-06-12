package com.example.demo.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.ProjectInformationDao;


@Repository
public class ProjectRepositoryJdbcImpl implements ProjectRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate; // 自動綁定 spring 內建的 JdbcTemplate 物件
	
	@Override
	public List<ProjectInformationDao> findAllProjects() {
		//String sql = "select * from book"; // 用 * 犯規
		String sql = "select projectId, projectName, department, userName, projectStatu from projectinformation";
		// BeanPropertyRowMapper(Book.class) 自動將每一筆紀錄注入到 Book 物件中
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ProjectInformationDao.class));
	}

	@Override
	public Optional<ProjectInformationDao> getProjectById(Integer projectId) {
		String sql = "select projectId, projectName, department, userName, projectStatu from projectinfromation where projectId=?";
		/**
		List<Book> books = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Book.class), id);
		return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
		*/
		try {
			// 查單筆
			ProjectInformationDao projectInformationDao = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ProjectInformationDao.class), projectId);
			return Optional.of(projectInformationDao);
		} catch (EmptyResultDataAccessException e) {
			// 沒查到資料會拋出例外
			return Optional.empty();
		}
		
	}

	@Override
	public boolean addProject(ProjectInformationDao projectInformationDao) {
		// 檢查 book.getPub() 是否是 null, 若是 null 則設定成 false
//		if(book.getPub() == null) {
//			book.setPub(false);
//		}
		String sql = "insert into projectinformation(projectName, department, userName, projectStatu) values(?, ?, ?, ?)";
		int rows = jdbcTemplate.update(sql, projectInformationDao.getProjectName(), projectInformationDao.getDepartment(), projectInformationDao.getUserName(), projectInformationDao.getProjectStatu());
		return rows > 0;
	}

	@Override
	public boolean updateProject(Integer projectId, ProjectInformationDao projectInformationDao) {
		// 檢查 book.getPub() 是否是 null, 若是 null 則設定成 false
//		if(book.getPub() == null) {
//			book.setPub(false);
//		}
		String sql = "update projectinformation set projectName = ?, department = ?, userName = ?, projectStatu = ? where projectId = ?";
		int rows = jdbcTemplate.update(sql, projectInformationDao.getProjectName(), projectInformationDao.getDepartment(), projectInformationDao.getUserName(), projectInformationDao.getProjectStatu(), projectId);
		return rows > 0;
	}

	@Override
	public boolean deleteProject(Integer projectId) {
		String sql = "delete from projectinformation where projectId = ?";
		int rows = jdbcTemplate.update(sql, projectId);
		return rows > 0;
		//return jdbcTemplate.update("delete from book where id = ?", id) > 0;
	}
}
