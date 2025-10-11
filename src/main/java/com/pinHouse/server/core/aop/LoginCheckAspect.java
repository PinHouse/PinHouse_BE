package com.pinHouse.server.core.aop;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.security.jwt.application.exception.JwtAuthenticationException;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoginCheckAspect {

    @Around("@annotation(com.pinHouse.server.core.aop.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {

        /// 먼저 인증 객체가 있는지 체크
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        /// 미인증(=null), 익명 토큰, Principal 타입 불일치 케이스 모두 차단
        if (auth == null ||
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken ||
                !(auth.getPrincipal() instanceof PrincipalDetails principal)) {

            /// 401 매핑
            throw new JwtAuthenticationException(ErrorCode.TOKEN_NOT_FOUND);
        }

        return pjp.proceed();
    }

}
