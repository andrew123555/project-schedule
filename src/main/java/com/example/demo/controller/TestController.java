package com.example.demo.controller; 

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600) 
@RestController 
@RequestMapping("/test") 
public class TestController {

    @GetMapping("/all") 
    public String allAccess() {
        return "Public Content.";
    }

   
}