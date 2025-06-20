package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogbackTest {

    private final Logger logger= LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void LogToSql(){
        logger.info("数据库日志info");
        logger.error("数据库日志error");
    }
}
