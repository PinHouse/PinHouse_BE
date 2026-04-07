package co.kr.pinhouse.common.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
import org.springframework.stereotype.Component;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

/**
 * 서비스 레이어의 실행을 모니터링하고 트레이스 데이터를 생성하기 위한 Aspect 클래스입니다.
 * Micrometer Observation API를 사용하여 메트릭과 분산 트레이싱을 동시에 처리합니다.
 */
@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnEnabledTracing
public class ServiceObservationAspect {

	private final ObservationRegistry observationRegistry;

	/**
	 * 지정된 패키지 경로에 포함된 모든 퍼블릭 서비스 메서드를 대상으로 관측(Observation)을 수행합니다.
	 * 대상:
	 * 1. co.kr.pinhouse.domain..application.service 패키지 하위
	 * 2. co.kr.pinhouse.security..application.service 패키지 하위
	 * 3. co.kr.pinhouse.security.oauth2.service 패키지 하위
	 */
	@Around("""
		execution(public * co.kr.pinhouse.domain..application.service..*.*(..))
		|| execution(public * co.kr.pinhouse.security..application.service..*.*(..))
		|| execution(public * co.kr.pinhouse.security.oauth2.service..*.*(..))
		""")
	public Object observeServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
		// 실행되는 메서드의 시그니처에서 클래스명과 메서드명을 추출합니다.
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		String className = signature.getDeclaringType().getSimpleName();
		String methodName = signature.getName();

		// "pinhouse.service"라는 이름으로 새로운 관측(Observation) 객체를 생성합니다.
		Observation observation = Observation.createNotStarted("pinhouse.service", observationRegistry)
			// 템플릿 이름(Span 이름)을 "클래스명#메서드명" 형식으로 지정합니다.
			.contextualName(className + "#" + methodName)
			// 인덱싱 및 필터링에 사용할 태그(Key-Value)를 추가합니다.
			.lowCardinalityKeyValue("layer", "service")
			.lowCardinalityKeyValue("service.class", className)
			.lowCardinalityKeyValue("service.method", methodName);

		// 관측 시작
		observation.start();

		// Scope를 오픈하여 현재 스레드에 트레이스 컨텍스트를 유지합니다 (ThreadLocal 관리).
		try (Observation.Scope scope = observation.openScope()) {
			// 실제 서비스 로직 실행
			return joinPoint.proceed();
		} catch (Throwable throwable) {
			// 예외 발생 시 관측 객체에 에러 정보를 기록합니다.
			observation.error(throwable);
			throw throwable;
		} finally {
			// 관측 종료 및 기록 완료 (이 시점에 데이터가 전송될 준비가 됩니다.)
			observation.stop();
		}
	}
}
