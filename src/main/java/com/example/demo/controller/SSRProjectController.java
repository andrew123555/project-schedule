package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.exception.ProjectException;
import com.example.demo.model.dto.ProjectInformationDao;
import com.example.demo.service.ProjectService;

@Controller
@RequestMapping("/ssr/project")
public class SSRProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	// 查詢所有書籍
	@GetMapping
	public String findAllProjects(Model model) {
		List<ProjectInformationDao> projects = projectService.findAllProjects();
		model.addAttribute("projects", projects); // 將要傳遞給 jsp 的資料放入 Model 容器中
		return "project-list"; // 對應到 /WEB-INF/view/book-list.jsp
	}
	
	// 新增書籍
	@PostMapping("/add")
	public String addProject(ProjectInformationDao projectInformationDao, Model model) {
		try {
			projectService.addProject(projectInformationDao);
		} catch (ProjectException e) {
			model.addAttribute("message", "新增錯誤: " + e.getMessage());
			return "error";
		}
		return "redirect:/ssr/project";
		//return "redirect:http://localhost:8080/ssr/book";
		//return "redirect:https://tw.yahoo.com";
	}
	
	// 刪除書籍
	
	@DeleteMapping("/delete/{projectId}")
	public String deleteProject(@PathVariable Integer projectId, Model model) {
		try {
			projectService.deleteProject(projectId);
		} catch (ProjectException e) {
			model.addAttribute("message", "刪除錯誤: " + e.getMessage());
			return "error";
		}
		return "redirect:/ssr/project";
	}
	
	// 取得修改頁面
	@GetMapping("/edit/{id}")
	public String getEditPage(@PathVariable Integer projectId, Model model) {
		try {
			ProjectInformationDao projectInformationDao = projectService.getProjectById(projectId);
			model.addAttribute("projects", projectInformationDao);
			return "project-edit";
		} catch (ProjectException e) {
			model.addAttribute("message", "查無該筆項目資料: " + e.getMessage());
			return "error";
		}
	}
	
	// 修改書籍
	//@PostMapping("/edit/{id}")
	@PutMapping("/edit/{projectId}")
	public String editPorject(@PathVariable Integer projectId, ProjectInformationDao projectInformationDao, Model model) {
		try {
			projectService.updateProject(projectId, projectInformationDao);
		} catch (ProjectException e) {
			model.addAttribute("message", "修改錯誤: " + e.getMessage());
			return "error";
		}
		return "redirect:/ssr/project";
	}
	
}