package co.kr.pinhouse.security.config;

import static co.kr.pinhouse.domain.user.domain.entity.Role.ADMIN;
import static co.kr.pinhouse.domain.user.domain.entity.Role.USER;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import co.kr.pinhouse.security.jwt.filter.JwtAuthenticationDeniedHandler;
import co.kr.pinhouse.security.jwt.filter.JwtAuthenticationFailureHandler;
import co.kr.pinhouse.security.jwt.filter.JwtAuthenticationFilter;
import co.kr.pinhouse.security.jwt.filter.RequestMatcherHolder;
import co.kr.pinhouse.security.oauth2.handler.OAuth2FailureHandler;
import co.kr.pinhouse.security.oauth2.handler.OAuth2SuccessHandler;
import co.kr.pinhouse.security.oauth2.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security 설정 클래스
 *
 * 애플리케이션의 인증 및 권한 부여 정책을 정의한다.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	/// OAUTH2 관련
	private final OAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;

	/// JWT 관련
	private final JwtAuthenticationFilter jwtFilter;
	private final JwtAuthenticationFailureHandler jwtFailureHandler;
	private final JwtAuthenticationDeniedHandler jwtDeniedHandler;

	/// 시큐리티 및 CORS
	private final RequestMatcherHolder requestMatcherHolder;
	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(null))
				.permitAll()
				.requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(USER))
				.hasAnyAuthority(ADMIN.getRole(), USER.getRole())
				.requestMatchers(requestMatcherHolder.getRequestMatchersByMinRole(ADMIN))
				.hasAnyAuthority(ADMIN.getRole())
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(oAuth2UserService)
				)
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> {
				exception.authenticationEntryPoint(jwtFailureHandler)
					.accessDeniedHandler(jwtDeniedHandler);
			});

		return http.build();
	}
}
