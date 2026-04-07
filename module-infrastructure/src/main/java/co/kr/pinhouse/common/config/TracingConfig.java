package co.kr.pinhouse.common.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import io.lettuce.core.resource.ClientResources;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry;
import io.opentelemetry.instrumentation.lettuce.v5_1.LettuceTelemetry;
import io.opentelemetry.instrumentation.mongo.v3_1.MongoTelemetry;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Hooks;

@Configuration
@ConditionalOnEnabledTracing
public class TracingConfig {

	private static final List<String> EXCLUDED_PATHS = List.of(
		"/actuator/**",
		"/actuator/health",
		"/actuator/info",
		"/actuator/prometheus",
		"/actuator/metrics/**"
	);

	@PostConstruct
	public void enableReactorContextPropagation() {
		Hooks.enableAutomaticContextPropagation();
	}

	@Bean
	public ObservationPredicate skipActuatorEndpointsFromTracing() {
		PathMatcher pathMatcher = new AntPathMatcher();

		return (name, context) -> {
			if (context instanceof ServerRequestObservationContext serverContext) {
				String path = serverContext.getCarrier().getRequestURI();
				boolean shouldExclude = EXCLUDED_PATHS.stream()
					.anyMatch(pattern -> pathMatcher.match(pattern, path));
				// true를 반환하면 observation을 수행, false를 반환하면 건너뜀
				return !shouldExclude;
			}
			return true;
		};
	}

	@Bean
	public ObservedAspect observedAspect(ObservationRegistry registry) {
		return new ObservedAspect(registry);
	}

	/**
	 * JDBC DataSource를 OpenTelemetry로 wrap하여 DB 쿼리 추적
	 */
	@Bean
	@ConditionalOnClass(name = "io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry")
	public BeanPostProcessor dataSourceInstrumentationBeanPostProcessor(OpenTelemetry openTelemetry) {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof DataSource) {
					return JdbcTelemetry.create(openTelemetry).wrap((DataSource)bean);
				}
				return bean;
			}
		};
	}

	/**
	 * Lettuce Redis 클라이언트를 OpenTelemetry로 계측하여 Redis 명령어 추적
	 */
	@Bean
	@ConditionalOnClass(name = "io.opentelemetry.instrumentation.lettuce.v5_1.LettuceTelemetry")
	public LettuceClientConfigurationBuilderCustomizer lettuceTracingCustomizer(OpenTelemetry openTelemetry) {
		return builder -> builder.clientResources(
			ClientResources.builder()
				.tracing(LettuceTelemetry.create(openTelemetry).newTracing())
				.build()
		);
	}

	/**
	 * MongoDB 커맨드를 OpenTelemetry로 계측하여 Mongo 쿼리/명령 추적
	 */
	@Bean
	@ConditionalOnClass(name = "io.opentelemetry.instrumentation.mongo.v3_1.MongoTelemetry")
	public MongoClientSettingsBuilderCustomizer mongoTracingCustomizer(OpenTelemetry openTelemetry) {
		return builder -> builder.addCommandListener(
			MongoTelemetry.create(openTelemetry).newCommandListener()
		);
	}
}
