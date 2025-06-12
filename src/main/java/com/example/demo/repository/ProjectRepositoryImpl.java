package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.ProjectInformationDao;


//@Repository
//@Primary
public class ProjectRepositoryImpl implements ProjectRepository {
	// InMemory 版
	
	private List<ProjectInformationDao> projects = new CopyOnWriteArrayList<>();
	
	// 初始資料有 4 本書
	{
		projects.add(new ProjectInformationDao(1, "aaa", "s", "s", false));
		projects.add(new ProjectInformationDao(2, "bbb", "s", "s", false));
		projects.add(new ProjectInformationDao(3, "ccc", "s", "s", true));
		projects.add(new ProjectInformationDao(4, "ddd", "pm", "s", true));
	}
	@Override
	public List<ProjectInformationDao> findAllProjects(){
		return projects;
	}
	@Override
	public Optional<ProjectInformationDao> getProjectById(Integer projectId) {
		return projects.stream().filter(project -> project.getProjectId().equals(projectId)).findFirst();
	}
	@Override
	public boolean addProject(ProjectInformationDao project) {
		// 建立 newId
		OptionalInt optMaxId = projects.stream().mapToInt(ProjectInformationDao::getProjectId).max();
		int newId = optMaxId.isEmpty() ? 1 : optMaxId.getAsInt() + 1;
		// 將 newId 設定給 book
		project.setProjectId(newId);
		
		return projects.add(project);
	}
	@Override
	public boolean updateProject(Integer projectId, ProjectInformationDao projectInformationDao) {
		// 找到要修改的 book
		Optional<ProjectInformationDao> optProject = getProjectById(projectId);
		if(optProject.isEmpty()) {
			return false;
		}
		// 找到該 book 在 books 的 index
		int index = projects.indexOf(optProject.get());
		if(index == -1) {
			return false;
		}
		// 替換
		return projects.set(index, projectInformationDao) != null;
	}
	@Override
	public boolean deleteProject(Integer projectId) {
		Optional<ProjectInformationDao> optProject = getProjectById(projectId);
		if(optProject.isPresent()) {
			return projects.remove(optProject.get());
		}
		return false;
	}
	


	



	

	
	
	
}