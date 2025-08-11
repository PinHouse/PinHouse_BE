package com.pinHouse.server.security.config;

import com.pinHouse.server.security.jwt.filter.JwtAuthenticationDeniedHandler;
import com.pinHouse.server.security.jwt.filter.JwtAuthenticationFailureHandler;
import com.pinHouse.server.security.jwt.filter.JwtAuthenticationFilter;
import com.pinHouse.server.security.oauth2.handler.OAuth2SuccessHandler;
import com.pinHouse.server.security.oauth2.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.pinHouse.server.platform.domain.user.Role.ADMIN;
import static com.pinHouse.server.platform.domain.user.Role.USER;

/**
 * Spring Security 설정 클래스
 *
 * 애플리케이션의 인증 및 권한 부여 정책을 정의한다.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationFailureHandler jwtFailureHandler;
    private final JwtAuthenticationDeniedHandler jwtDeniedHandler;
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
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/login?error");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(jwtFailureHandler)
                            .accessDeniedHandler(jwtDeniedHandler);
                });


        return http.build();
    }
}
