package co.kr.pinhouse.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

	@Value("${cors.front.local}")
	private String frontLocal;

	@Value("${cors.front.dev}")
	private String frontDev;

	@Value("${cors.front.prod}")
	private String frontProd;

	@Value("${cors.front.admin-local:}")
	private String adminFrontLocal;

	@Value("${cors.front.admin-dev:}")
	private String adminFrontDev;

	@Value("${cors.front.admin-prod:}")
	private String adminFrontProd;

	@Value("${cors.back.dev}")
	private String backDev;

	/**
	 * CORS 설정을 진행합니다.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		/// CORS 추가
		configuration.addAllowedOriginPattern(frontLocal);
		configuration.addAllowedOriginPattern(frontDev);
		configuration.addAllowedOriginPattern(frontProd);
		addAllowedOriginIfPresent(configuration, adminFrontLocal);
		addAllowedOriginIfPresent(configuration, adminFrontDev);
		addAllowedOriginIfPresent(configuration, adminFrontProd);
		configuration.addAllowedOriginPattern(backDev);

		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);

		configuration.addExposedHeader("Authorization");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private void addAllowedOriginIfPresent(CorsConfiguration configuration, String origin) {
		if (origin != null && !origin.isBlank()) {
			configuration.addAllowedOriginPattern(origin);
		}
	}

}
