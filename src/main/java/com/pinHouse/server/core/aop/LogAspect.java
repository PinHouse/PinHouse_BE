package com.pinHouse.server.core.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
@Slf4j
public class LogAspect {

    /// 서비스 계층 응답
    @Pointcut("execution(* com.pinHouse.server.platform..service.*Service.*(..))")
    private void applicationLayer() {

    }

    /// 성능 시간 테스트
    @Around("applicationLayer()")
    public Object logProcessTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // 실제 메서드 실행
        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info("[서비스 로깅] 메서드 실행 시간: {}.{} 실행 시간 = {}ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }

    @AfterThrowing(pointcut = "applicationLayer()")
    public void logException(JoinPoint joinPoint) {
        log.info("[서비스 로깅] 예외 발생: {},{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

}
