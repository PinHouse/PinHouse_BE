package co.kr.pinhouse.security.auth.application.service;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.exception.code.SecurityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.util.KeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeCodeRateLimitService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${auth.exchange.rate-limit.max-attempts:10}")
	private long maxAttempts;

	@Value("${auth.exchange.rate-limit.window-seconds:60}")
	private long windowSeconds;

	public void validateRequestAllowed(String clientIdentifier) {
		String normalizedClientIdentifier = normalizeClientIdentifier(clientIdentifier);
		String key = KeyUtil.getExchangeCodeRateLimitKey(normalizedClientIdentifier);
		Long requestCount = redisTemplate.opsForValue().increment(key);

		if (requestCount == null) {
			throw new IllegalStateException("Exchange Code rate limit 카운트 증가에 실패했습니다.");
		}

		if (requestCount == 1L) {
			redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
		}

		if (requestCount > maxAttempts) {
			log.warn("Exchange Code 교환 요청 제한 초과 - clientIdentifier: {}, count: {}",
				sanitize(normalizedClientIdentifier), sanitize(requestCount));
			throw new CustomException(SecurityErrorCode.EXCHANGE_CODE_RATE_LIMITED);
		}
	}

	private String normalizeClientIdentifier(String clientIdentifier) {
		if (clientIdentifier == null || clientIdentifier.isBlank()) {
			return "unknown";
		}
		return clientIdentifier.trim();
	}
}
