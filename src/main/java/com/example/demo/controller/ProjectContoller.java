package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDao;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ProjectService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/rest/project")
public class ProjectContoller {

    
	@Autowired
	private ProjectService projectService;

    
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProjectInformationDao>>> findAllProjects() {
		List<ProjectInformationDao> projects = projectService.findAllProjects();
		if(projects.size() == 0) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,"查無此項目"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功:", projects));
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationDao>> getProjectById(@PathVariable Integer projectId) {
		try {
			ProjectInformationDao projectInformationDao = projectService.getProjectById(projectId);
			return ResponseEntity.ok(ApiResponse.success("查詢成功:", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<ProjectInformationDao>> addProject(@RequestBody ProjectInformationDao projectInformationDao) {
		try {
			projectService.addProject(projectInformationDao);
			return ResponseEntity.ok(ApiResponse.success("新增成功", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	@DeleteMapping("/{projectId}")
	public ResponseEntity<ApiResponse<String>> deletedProject(@PathVariable Integer projectId) {
		try {
			projectService.deleteProject(projectId);
			return ResponseEntity.ok(ApiResponse.success("刪除成功", ""));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	@PutMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationDao>> updateProject(@PathVariable Integer projectId, @RequestBody ProjectInformationDao projectInformationDao) {
		try {
			projectService.updateProject(projectId, projectInformationDao);
			return ResponseEntity.ok(ApiResponse.success("修改成功", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	

	
	@PatchMapping("/name/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationDao>> updateProjectName(@PathVariable Integer projectId, @RequestBody ProjectInformationDao projectInformationDao) {
		try {
			projectService.updateProjectName(projectId, projectInformationDao.getProjectName());
			return ResponseEntity.ok(ApiResponse.success("修改項目名稱成功", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	} 
	
		
	
	
}
