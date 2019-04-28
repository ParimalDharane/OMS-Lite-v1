package com.parimal.omslite.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTrackingAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before(value = "execution(* com.parimal.omslite.services.*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        logger.info("Before method={} args={}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @After(value = "execution(* com.parimal.omslite.services.*.*(..))")
    public void afterAdvice(JoinPoint joinPoint) {
        logger.info("After method: {}", joinPoint.getSignature());
    }
}