package com.pinHouse.server.core.config;

import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    /// 해당 기능을 사용한 사람을 감시하는 Config
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {

            // 시큐리티에서 현재 사용자가 누구인지 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                /// 인증 정보가 없다면
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof PrincipalDetails details) {
                /// 인증 정보가 있다면
                return Optional.of(details.getName());
            }

            // 기본값
            return Optional.empty();
        };
    }
}
