package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LogController {
	
	@GetMapping("/log")
	public String index() {
		log.info("日誌無參數輸出(info)");
		log.trace("日誌輸出(trace):{}" , "/log");
		log.info("日誌輸出(info):{}" , "/log");
		log.debug("日誌輸出(debug):{}" , "/log");
		log.warn("日誌輸出(warn):{}" , "/log");
		log.error("日誌輸出(error):{}" , "/log");
		return "log";
	}
}
