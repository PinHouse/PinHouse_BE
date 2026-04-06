package co.kr.pinhouse.api.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {

	/// 서비스 계층
	@Pointcut("execution(* co.kr.pinhouse.domain..application..*Service*.*(..))")
	private void applicationLayer() {
	}

	/// 인증 계층
	@Pointcut("execution(* co.kr.pinhouse.security..application..*Service*.*(..))")
	private void authLayer() {
	}

}
