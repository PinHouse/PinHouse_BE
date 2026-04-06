package com.pinHouse.security.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.pinHouse.common.exception.code.CommonErrorCode;
import com.pinHouse.common.response.CustomException;
import com.pinHouse.security.oauth2.domain.PrincipalDetails;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoginCheckAspect {

	/// 한번 더 체크하는 AOP
	@Around("@annotation(com.pinHouse.common.aop.CheckLogin)")
	public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {

		/// 먼저 인증 객체가 있는지 체크
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		/// 미인증(=null), 익명 토큰, Principal 타입 불일치 케이스 모두 차단
		if (auth == null ||
				!auth.isAuthenticated() ||
				auth instanceof AnonymousAuthenticationToken ||
				!(auth.getPrincipal() instanceof PrincipalDetails principal)) {

			/// 401 매핑
			throw new CustomException(CommonErrorCode.UNAUTHORIZED);
		}

		return pjp.proceed();
	}

}
