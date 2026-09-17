package com.taskmanager.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.taskmanager.service..*(..))")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("→ Entering: {} | Args: {}", methodName, Arrays.toString(args));
        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("✗ Exception in: {} | {}ms | Error: {}", methodName, elapsed, ex.getMessage());
            throw ex;
        }

        long elapsed = System.currentTimeMillis() - start;
        log.debug("✓ Exiting: {} | {}ms", methodName, elapsed);
        return result;
    }
}
