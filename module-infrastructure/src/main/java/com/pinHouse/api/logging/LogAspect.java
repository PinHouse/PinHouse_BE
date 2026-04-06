package com.pinHouse.api.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {

	/// 서비스 계층
	@Pointcut("execution(* com.pinHouse.domain..application..*Service*.*(..))")
	private void applicationLayer() {}

	/// 인증 계층
	@Pointcut("execution(* com.pinHouse.security..application..*Service*.*(..))")
	private void authLayer() {}

}
