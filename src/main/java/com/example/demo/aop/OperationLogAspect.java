package com.example.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.demo.util.OperationLog;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
public class OperationLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogAspect.class);

    
    @Pointcut("@annotation(com.example.demo.util.OperationLog)")
    public void logPointCut() {
    }
    
    Long longTimeStampStart, longTimeStampEnd;

    @Before("logPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        
    	long currentTimeMillis = System.currentTimeMillis();
    	java.util.Date date = new java.util.Date(currentTimeMillis);
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String formattedDate = sdf.format(date);
    	//java.util.Date date = new java.util.Date(currentTimeMillis);
    	//longTimeStampStart = System.currentTimeMillis();
    	// 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        String operation = operationLog.value();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        // 记录日志
        logger.info("操作日誌 - 時間: {}, 方法: {}, 操作: {}, 参数: {}", formattedDate ,method.getName(), operation, args);
    }
 // 帶入JoinPoint表示被執行的那個Pointcut程式類別
    // @AfterReturning表示在Controller正常完成之後運作
    @AfterReturning("LoggingAspectPointcut()")
    public void AfterReturningOfPointcut(JoinPoint joinPoint) {
        // 在日誌輸出Controller正常執行完成的資訊
        Logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), "API Finished Normally.");
    }
 
    // @AfterThrowing表示Controller發生錯誤時要做的事情
    @AfterThrowing(value = "LoggingAspectPointcut()", throwing = "exception")
    public void AfterThrowingOfPointcut(JoinPoint joinPoint, Exception exception) {
        Logger myLogger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
 
        // 在日誌輸出Exception資訊
        myLogger.error("API Finished by Exception: {}", exception.getMessage());
    }
 
    // @After表示Controller無論有沒有正常結束都要做的事情
    @After("LoggingAspectPointcut()")
    public void AfterPointcut(JoinPoint joinPoint) {
        // 紀錄Controller結束時間
        longTimeStampEnd = System.currentTimeMillis();
 
        // 起訖時間相減取得該Controller的執行時間
        Long longTimeDiff = longTimeStampEnd - longTimeStampStart;
 
        // 在日誌輸出Controller執行時間
        Logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()),
                "Time used: " + Objects.toString(longTimeDiff) + " ms");
 
        // 在日誌輸出Request相關資訊
        Logger(LoggerFactory.getLogger(joinPoint.getTarget().getClass()), joinPoint);
    }
 
    // 注入HttpServletRequest以便取得Request資訊
    @Autowired
    private HttpServletRequest request;
 
    // 注入HttpServletResponse以便取得Response資訊
    @Autowired
    private HttpServletResponse response;
 
    private void Logger(Logger myLogger, JoinPoint joinPoint) {
        String protocol = Objects.toString(request.getProtocol(), "");
        String method = request.getMethod();
        String scheme = request.getScheme();
        String uri = request.getRequestURI();
        String agent = request.getHeader("user-agent");
        String remoteaddr = request.getRemoteAddr();
        int remoteaport = request.getRemotePort();
        String localaddr = request.getLocalAddr();
        int localport = request.getLocalPort();
 
        String sessionid = request.getSession().getId();
        String classname = joinPoint.getTarget().getClass().getName();
        String classmethod = joinPoint.getSignature().getName();
        String strMessage = "\r\nController Logging Message:\r\n";
        
        strMessage += String.format("%s\r\n", "---------------------------------------------------");
        strMessage += String.format("  Session ID: %s\r\n", sessionid);
        strMessage += String.format("    Protocol: %s\r\n", protocol);
        strMessage += String.format("      Method: %s\r\n", method);
        strMessage += String.format("         Url: %s://%s:%d%s\r\n", scheme, localaddr, localport, uri);
        strMessage += String.format("       Agent: %s\r\n", agent);
        strMessage += String.format("      Remote: %s:%d\r\n", remoteaddr, remoteaport);
        strMessage += String.format("  Class Name: %s\r\n", classname);
        strMessage += String.format("Class Method: %s", classmethod);
 
        myLogger.info(strMessage);
    }
 
    private void Logger(Logger myLogger, String message) {
        myLogger.info(message);
        myLogger.debug(message);
        myLogger.trace(message);
        myLogger.error(message);
    }
    
    
    
    
}