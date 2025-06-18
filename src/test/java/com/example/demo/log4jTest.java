package com.example.demo;

	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;

	public class log4jTest 
	{

	    public static final Logger logger = LoggerFactory.getLogger(log4jTest.class);

	    public static void main( String[] args )
	    {
	        System.out.println( "Hello World!" );
	        logger.info("slf4j for info");
	        logger.debug("slf4j for debug");
	        logger.error("slf4j for error");
	        logger.warn("slf4j for warn");

	        String message = "Hello SLF4J";
	        logger.info("slf4j message is : {}", message);
	    }
	}