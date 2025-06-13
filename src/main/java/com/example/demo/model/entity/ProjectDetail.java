package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ProjectDetail")
public class ProjectDetail {
	
	    @Id
	    @GeneratedValue
	    private Long toDoListId;
	    
	    @Column(name = "classification", length = 20)
	    private String classification;
	    
	    @Column(name = "toDoList", length = 20)
	    private String toDoList;
	    	    
	    @Column(name = "scheduleStartTime", length = 20)
	    private String scheduleStartTime;
	    
	    @Column(name = "scheduleEndTime", length = 20)
	    private String scheduleEndTime;
	    
	    @Column(name = "userName", length = 20)
	    private String userName;
	    
	    @Column(name = "confidential", length = 4)
	    private boolean confidential;
	
}
