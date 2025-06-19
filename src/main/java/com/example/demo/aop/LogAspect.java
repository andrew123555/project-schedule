package com.example.demo.aop;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.example.demo.model.entity.Log;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut("@annotation(com.example.demo.model.entity.Log)")
    private void logPointCut() {
    }
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        String logDesc = StringUtils.isBlank(logAnnotation.value()) ? "未知方法" : logAnnotation.value();
        log.info("【日誌注解】开始执行 -- {}:{} {}", className, methodName, logDesc);
        Object result = joinPoint.proceed();
        log.info("【日誌注解】执行结束 -- {}:{} {}", className, methodName, logDesc);
        return result;
    }
}