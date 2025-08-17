package com.paymentservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

    @Around("execution(* com.paymentservice.app.controller.*.*(..)) && @within(org.springframework.web.bind.annotation.RestController)")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        logger.info("Entering " + methodName + " with args: " + Arrays.toString(args));

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // actual method execution
        long duration = System.currentTimeMillis() - start;

        logger.info("Exiting " + methodName + " | Execution time: " + duration + " ms | Response: " + result);

        return result;
    }
}
