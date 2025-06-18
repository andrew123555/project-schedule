package com.example.demo.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Table;
import lombok.Data;


@Table(name = "log")
@Data
public class Log implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
     private Long id;
	 
	 
     private String level;
     private String message;
     private String logger;
     private String thread;
     private LocalDateTime timestamp;
     private String method ;
    // private Object args;
     private String operation;
}
