//package com.munity.config;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonSerializable;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.util.JSONPObject;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * log aspect
// */
//@Slf4j
//@Aspect
//@Component
//public class AopLogConfig {
//
//    private long startTime;
//
//    @Pointcut("execution(public * com.munity.controller..*(..))")
//    public void logPointCut() {
//    }
//
//    @Before("logPointCut()")
//    public void doBefore(JoinPoint joinPoint) throws JsonProcessingException {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = attributes.getRequest();
//
//        startTime = System.currentTimeMillis();
//        log.info("url={}||method={}||args={}", request.getRequestURL().toString(), request.getMethod(), new ObjectMapper().writeValueAsString(joinPoint.getArgs()));
//    }
//
//    @AfterReturning(pointcut = "logPointCut()", returning = "returnObj")
//    public void doAfterReturning(Object returnObj) throws JsonProcessingException {
////        log.info("costTime={}||resp={}", System.currentTimeMillis() - startTime, new ObjectMapper().writeValueAsString(returnObj));
//        log.info("costTime={}||resp={}", System.currentTimeMillis() - startTime, new ObjectMapper().writeValueAsString(returnObj));
//    }
//
//    @AfterThrowing(pointcut = "logPointCut()", throwing = "throwable")
//    public void doAfterThrowing(Throwable throwable) {
//        log.error("errorMsg={}", throwable.getMessage());
//        log.error("errorTrace={}", getStackTraceByPn(throwable));
//    }
//
//    public static String getStackTraceByPn(Throwable e) {
//        StringBuilder s = new StringBuilder("\n").append(e);
//        for (StackTraceElement traceElement : e.getStackTrace()) {
//            s.append("\n\tat ").append(traceElement);
//        }
//        return s.toString();
//    }
//
//}