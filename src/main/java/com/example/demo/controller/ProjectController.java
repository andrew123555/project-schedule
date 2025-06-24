package com.example.demo.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationBDto;
import com.example.demo.model.dto.ProjectInformationDDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ProjectIMService;
import com.example.demo.service.ProjectService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/rest/project")
public class ProjectController {

	

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ProjectIMService projectIMService;
    

	@GetMapping
	public ResponseEntity<ApiResponse<List<ProjectInformationBDto>>> findAllProjects() {
		List<ProjectInformationBDto> projects = projectService.findAllProjects();
		if(projects.size() == 0) {
			
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,"查無此項目"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功TTTEST:", projects));
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationBDto>> getProjectById(@PathVariable Integer projectId) {
		try {
			ProjectInformationBDto projectInformationDao = projectService.getProjectById(projectId);
			return ResponseEntity.ok(ApiResponse.success("查詢成功:", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	
	
	@PostMapping
	public ResponseEntity<ApiResponse<ProjectInformationBDto>> addProject(@RequestBody ProjectInformationBDto projectInformationDao) {
		try {
			projectService.addProject(projectInformationDao);
			return ResponseEntity.ok(ApiResponse.success("新增成功", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	
	@DeleteMapping("/{projectId}")
	public ResponseEntity<ApiResponse< String>> deletedProject(@PathVariable Integer projectId) {
		try {
			projectService.deleteProject(projectId);
			return ResponseEntity.ok(ApiResponse.success("刪除成功", ""));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	
	@PutMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationBDto>> updateProject(@PathVariable Integer projectId, @RequestBody ProjectInformationBDto projectInformationDao) {
		try {
			projectService.updateProject(projectId, projectInformationDao);
			return ResponseEntity.ok(ApiResponse.success("修改成功", projectInformationDao));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	

	
	
	//項目詳細資訊----------------------------------------------------------
	@GetMapping("/item")
	public ResponseEntity<ApiResponse<List<ProjectInformationDDto>>> findAllProjectIMs() {
		List<ProjectInformationDDto> projectIMs = projectIMService.findAllProjectIMs();
		if(projectIMs.size() == 0) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,"查無此項目"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功TTTEST:", projectIMs));
	}
	//查詢單筆
	@GetMapping("/item/IM/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInformationDDto>> getProjectIMById(@PathVariable Integer projectId) {
		try {
			ProjectInformationDDto projectInformationDDto = projectIMService.getProjectIMById(projectId);
			return ResponseEntity.ok(ApiResponse.success("查詢成功:", projectInformationDDto));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	
	
	@PostMapping("/item")
	public ResponseEntity<ApiResponse<ProjectInformationDDto>> addProjectIM(@RequestBody ProjectInformationDDto projectInformationDDto) {
		try {
			projectIMService.addProjectIM(projectInformationDDto);
			return ResponseEntity.ok(ApiResponse.success("新增成功", projectInformationDDto));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	@DeleteMapping("/item/{toDoListId}")
	public ResponseEntity<ApiResponse<String>> deletedProjectIM(@PathVariable Integer toDoListId) {
		try {
			projectIMService.deleteProjectIM(toDoListId);
			return ResponseEntity.ok(ApiResponse.success("刪除成功", ""));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401 ,e.getMessage()));
		}
	}
	
	@PutMapping("/item/{toDoListId}")
	public ResponseEntity<ApiResponse<ProjectInformationDDto>> updateProjectIM(@PathVariable Integer toDoListId, @RequestBody ProjectInformationDDto projectInformationDDto) {
		try {
			projectIMService.updateProjectIM(toDoListId, projectInformationDDto);
			return ResponseEntity.ok(ApiResponse.success("修改成功", projectInformationDDto));
		} catch (ProjectException e) {
			return ResponseEntity.badRequest().body(ApiResponse.error(401,e.getMessage()));
		}
	}
	

	
	
}
